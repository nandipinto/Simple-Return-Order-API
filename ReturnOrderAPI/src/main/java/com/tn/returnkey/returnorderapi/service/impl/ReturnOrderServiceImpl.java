package com.tn.returnkey.returnorderapi.service.impl;

import com.tn.returnkey.returnorderapi.constants.ResponseMessage;
import com.tn.returnkey.returnorderapi.constants.ReturnOrderItemStatus;
import com.tn.returnkey.returnorderapi.constants.ReturnOrderStatus;
import com.tn.returnkey.returnorderapi.entity.Order;
import com.tn.returnkey.returnorderapi.entity.ReturnOrder;
import com.tn.returnkey.returnorderapi.entity.ReturnOrderDetail;
import com.tn.returnkey.returnorderapi.entity.ReturnOrderToken;
import com.tn.returnkey.returnorderapi.entity.TokenKey;
import com.tn.returnkey.returnorderapi.model.InitiateReturnOrderRequest;
import com.tn.returnkey.returnorderapi.model.InitiateReturnOrderResponse;
import com.tn.returnkey.returnorderapi.model.ReturnOrderItem;
import com.tn.returnkey.returnorderapi.model.ReturnOrderRequest;
import com.tn.returnkey.returnorderapi.model.ReturnOrderResponse;
import com.tn.returnkey.returnorderapi.model.ReturnOrderResponseDetail;
import com.tn.returnkey.returnorderapi.model.UpdateReturnOrderItemStatusResponse;
import com.tn.returnkey.returnorderapi.repo.OrderRepository;
import com.tn.returnkey.returnorderapi.repo.ReturnOrderDetailRepository;
import com.tn.returnkey.returnorderapi.repo.ReturnOrderRepository;
import com.tn.returnkey.returnorderapi.repo.ReturnOrderTokenRepository;
import com.tn.returnkey.returnorderapi.service.ReturnOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReturnOrderServiceImpl implements ReturnOrderService {

    @Autowired
    private ReturnOrderTokenRepository returnOrderTokenRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ReturnOrderRepository returnOrderRepository;
    @Autowired
    private ReturnOrderDetailRepository returnOrderDetailRepository;

    @Override
    public InitiateReturnOrderResponse initiateReturnOrder(InitiateReturnOrderRequest request) {
        InitiateReturnOrderResponse response = new InitiateReturnOrderResponse();
        try {
            //validate return order request against order data
            Long count = orderRepository.countByOrderIdAndEmail(request.getOrderId(), request.getEmail());
            if (count == 0) {
                log.warn("No matching order found with orderId: {} and email: {}", request.getOrderId(), request.getEmail());
                response.setMessage(ResponseMessage.NO_MATCHING_RECORD_FOUND);
                return response;
            }

            //check for existing token for the given orderId & email in case API getting called more than once
            String token = null;
            Optional<ReturnOrderToken> optToken = returnOrderTokenRepository.findById(new TokenKey(request.getOrderId(), request.getEmail()));
            if (optToken.isPresent()) {
                log.info("Found existing token for orderId: {}  and email: {}", request.getOrderId(), request.getEmail());
                token = optToken.get().getToken();
            } else {
                log.info("No existing token found for orderId: {}  and email: {}", request.getOrderId(), request.getEmail());
                token = returnOrderTokenRepository.save(new ReturnOrderToken(
                        new TokenKey(request.getOrderId(), request.getEmail()), UUID.randomUUID().toString()
                )).getToken();
            }
            response.setToken(token);
            response.setSuccessful(true);
        }
        catch (Exception e) {
            log.error("Failed to initiate return order!", e);

            response.setMessage(ResponseMessage.INTERNAL_SYSTEM_ERROR);
        }
        return response;
    }

    @Override
    public ReturnOrderResponse createReturnOrder(ReturnOrderRequest returnOrderRequest) {

        ReturnOrderResponse response = new ReturnOrderResponse();
        try {
            if (!StringUtils.hasText(returnOrderRequest.getToken())) {
                log.error("Return order request does not contain token!");

                response.setMessage(ResponseMessage.BAD_REQUEST);
                return response;
            }

            ReturnOrderToken token = lookupToken(returnOrderRequest.getToken());
            if (token == null){
                log.error("Invalid request token: {}", returnOrderRequest.getToken());

                response.setMessage(ResponseMessage.INVALID_REQUEST_TOKEN);
                return response;
            }

            if (returnOrderRequest.getItems().isEmpty()) {
                log.error("No item specified in return order request!");

                response.setMessage(ResponseMessage.BAD_REQUEST);
                return response;
            }

            String orderId = token.getTokenKey().getOrderId();
            //validate items to be returned must be matching with existing orders
            List<ReturnOrderItem> returnOrderItems = returnOrderRequest.getItems().stream()
                    .filter(item -> item.getOrderId().equalsIgnoreCase(orderId))
                    .collect(Collectors.toList());

            if (returnOrderItems.isEmpty()){
                log.error("No matching order found with orderId: {}", orderId);
                response.setSuccessful(false);
                response.setMessage(ResponseMessage.NO_MATCHING_RECORD_FOUND);

                return response;
            }

            log.debug("Items requested to be returned: {}", returnOrderRequest.getItems());

            ReturnOrderResponseDetail responseDetail = new ReturnOrderResponseDetail();
            double estimatedRefundAmount = 0.0;

            ReturnOrder returnOrder = ReturnOrder.builder()
                    .token(returnOrderRequest.getToken()).orderId(orderId)
                    .status(ReturnOrderStatus.AWAITING_APPROVAL)
                    .details(new ArrayList<>()).build();

            for (ReturnOrderItem item : returnOrderItems){
                Optional<Order> optOrder = orderRepository.findOneByOrderIdAndSku(orderId, item.getSku());
                if (!optOrder.isPresent()){
                    log.warn("Return order item with SKU: {} does not exist, skipping.", item.getSku());
                    item.setStatus(ReturnOrderItemStatus.INVALID_SKU);
                    responseDetail.getItems().add(item);

                } else {
                    Order order = optOrder.get();
                    //check if an item returned more than once
                    Optional<ReturnOrderDetail> optRod = returnOrderDetailRepository.findOneByReturnOrderOrderIdAndSku(orderId, item.getSku());
                    if (optRod.isPresent()) {
                        log.warn("Order item with SKU: {} has already be returned previously, skipping.", item.getSku());

                        ReturnOrderDetail dtl = optRod.get();
                        item.setItemId(dtl.getId());
                        item.setPrice(dtl.getPrice());
                        item.setStatus(ReturnOrderItemStatus.ALREADY_RETURNED);

                        responseDetail.getItems().add(item);

                    } else {
                        //check if returned quantity is > ordered quantity
                        if (item.getQuantity() > order.getQuantity()){
                            log.warn("Returned quantity:{} for item: {} is greater than ordered quantity: {}, skipping", item.getQuantity(), item.getSku(), order.getQuantity());

                            item.setStatus(ReturnOrderItemStatus.REJECTED);
                            responseDetail.getItems().add(item);
                        } else {
                            ReturnOrderDetail returnOrderDetail = ReturnOrderDetail.builder().returnOrder(returnOrder)
                                    .sku(item.getSku()).quantity(item.getQuantity()).price(order.getPrice()).build();
                            returnOrder.getDetails().add(returnOrderDetail);

                            estimatedRefundAmount = estimatedRefundAmount + (item.getQuantity() * order.getPrice());
                        }
                    }
                }
            }
            log.debug("Items to be returned: {}", returnOrder.getDetails());
            if (!returnOrder.getDetails().isEmpty()){
                ReturnOrder result = returnOrderRepository.save(returnOrder);
                log.debug("Successfully saving return order: {}", returnOrder);

                responseDetail.getItems().addAll(toReturnOrderItems(result.getDetails()));
                responseDetail.setRefundAmount(estimatedRefundAmount);

                response.setReturnOrderId(result.getId());
                response.setMessage(ResponseMessage.REQUEST_AWAITING_APPROVAL);
                response.setStatus(ReturnOrderStatus.AWAITING_APPROVAL);
                response.setSuccessful(true);
            } else {
                if (responseDetail.getItems().isEmpty()){
                    response.setMessage(ResponseMessage.NO_MATCHING_ORDER_ITEMS_FOUND);
                }
            }
            response.setDetail(responseDetail);
        }
        catch (Exception e) {
            log.error("Failed to initiate return order!", e);
            response.setMessage(ResponseMessage.INTERNAL_SYSTEM_ERROR);
        }
        return response;
    }

    @Override
    public ReturnOrderResponse getReturnOrder(Long returnOrderId) {
        ReturnOrderResponse response = new ReturnOrderResponse();

        try {
            Optional<ReturnOrder> opt = returnOrderRepository.findById(returnOrderId);
            if (!opt.isPresent()){
                response.setMessage(ResponseMessage.NO_MATCHING_RECORD_FOUND);
                return response;
            }

            ReturnOrder returnOrder = opt.get();
            response.setReturnOrderId(returnOrderId);
            response.setStatus(returnOrder.getStatus());
            response.setSuccessful(true);

            ReturnOrderResponseDetail detail = new ReturnOrderResponseDetail();
            detail.getItems().addAll(toReturnOrderItems(returnOrder.getDetails()));
            detail.setRefundAmount(countTotalRefundAmount(returnOrder.getDetails()));

            response.setDetail(detail);

        } catch (Exception e){
            log.error("Failed to initiate return order!", e);
            response.setMessage(ResponseMessage.INTERNAL_SYSTEM_ERROR);
        }
        return response;
    }

    @Override
    public UpdateReturnOrderItemStatusResponse updateItemStatus(Long returnOrderId, String sku, String status) {
        UpdateReturnOrderItemStatusResponse response = new UpdateReturnOrderItemStatusResponse();
        response.setReturnOrderId(returnOrderId);

        try {
            //check if there's existing return order matching the given id
            Optional<ReturnOrder> optRO = returnOrderRepository.findById(returnOrderId);
            if (!optRO.isPresent()){
                response.setMessage(ResponseMessage.NO_MATCHING_RECORD_FOUND);
                return response;
            }
            //make sure given status is valid item status
            if (!StringUtils.hasText(status)){
                response.setMessage(ResponseMessage.INVALID_PARAMETER_VALUE);
                return response;
            }
            ReturnOrderItemStatus itemStatus = lookupItemStatus(status);
            if (itemStatus == null){
                response.setMessage(ResponseMessage.INVALID_PARAMETER_VALUE);
                return response;
            }

            ReturnOrder returnOrder = optRO.get();
            ReturnOrderDetail detail = returnOrder.getDetails().stream().filter(d -> d.getSku().equalsIgnoreCase(sku)).findFirst().orElse(null);
            if (detail == null){
                response.setMessage(ResponseMessage.NO_MATCHING_RECORD_FOUND);
                return response;
            }
            detail.setStatus(itemStatus);
            returnOrderRepository.save(returnOrder);
            response.setSuccessful(true);
            response.setItem(toReturnOrderItem(detail));

            //if all items in this return order already QC-ed, set return order status to COMPLETE
            long itemsCount = returnOrder.getDetails().size();
            long qcedItemCount = returnOrder.getDetails().stream().filter(d -> d.getStatus() != null).count();
            if (qcedItemCount >= itemsCount){
                returnOrder.setStatus(ReturnOrderStatus.COMPLETE);
                returnOrderRepository.save(returnOrder);

                response.setMessage(ResponseMessage.REQUEST_COMPLETED);
            }
        } catch (Exception e){
            log.error("Failed to update item status!", e);
            response.setMessage(ResponseMessage.INTERNAL_SYSTEM_ERROR);
        }
        return response;
    }

    private double countTotalRefundAmount(List<ReturnOrderDetail> details){
        return details.stream().mapToDouble(
                d -> d.getStatus() == null || d.getStatus().equals(ReturnOrderItemStatus.ACCEPTED) ? d.getQuantity() * d.getPrice() : 0
        ).sum();
    }

    private ReturnOrderItemStatus lookupItemStatus(String status){
        if (!StringUtils.hasText(status)) return null;
        for (ReturnOrderItemStatus itemStatus : ReturnOrderItemStatus.values()){
            if (itemStatus.name().equalsIgnoreCase(status)) return itemStatus;
        }
        return null;
    }

    private List<ReturnOrderItem> toReturnOrderItems(List<ReturnOrderDetail> details){
        List<ReturnOrderItem> returnOrderItems = new ArrayList<>();
        for (ReturnOrderDetail rod : details){
            ReturnOrderItem item = toReturnOrderItem(rod);
            if (item != null) returnOrderItems.add(item);
        }
        return returnOrderItems;
    }

    private ReturnOrderItem toReturnOrderItem(ReturnOrderDetail rod){
        if (rod == null) return null;

        return ReturnOrderItem.builder().itemId(rod.getId()).orderId(rod.getReturnOrder().getOrderId())
                .sku(rod.getSku()).status(rod.getStatus()).quantity(rod.getQuantity()).price(rod.getPrice()).build();
    }

    private ReturnOrderToken lookupToken(String token){
        Optional<ReturnOrderToken> optToken = returnOrderTokenRepository.findOneByToken(token);
        return optToken.isPresent() ? optToken.get() : null;
    }
}

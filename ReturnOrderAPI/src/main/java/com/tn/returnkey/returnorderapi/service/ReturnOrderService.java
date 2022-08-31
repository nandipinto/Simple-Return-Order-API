package com.tn.returnkey.returnorderapi.service;

import com.tn.returnkey.returnorderapi.model.InitiateReturnOrderRequest;
import com.tn.returnkey.returnorderapi.model.InitiateReturnOrderResponse;
import com.tn.returnkey.returnorderapi.model.ReturnOrderRequest;
import com.tn.returnkey.returnorderapi.model.ReturnOrderResponse;
import com.tn.returnkey.returnorderapi.model.UpdateReturnOrderItemStatusResponse;

public interface ReturnOrderService {
    InitiateReturnOrderResponse initiateReturnOrder(InitiateReturnOrderRequest request);

    ReturnOrderResponse createReturnOrder(ReturnOrderRequest returnOrderRequest);

    ReturnOrderResponse getReturnOrder(Long returnOrderId);

    UpdateReturnOrderItemStatusResponse updateItemStatus(Long returnOrderId, String sku, String status);
}

package com.tn.returnkey.returnorderapi.controller;

import com.tn.returnkey.returnorderapi.constants.ReturnOrderItemStatus;
import com.tn.returnkey.returnorderapi.model.InitiateReturnOrderRequest;
import com.tn.returnkey.returnorderapi.model.InitiateReturnOrderResponse;
import com.tn.returnkey.returnorderapi.model.ReturnOrderRequest;
import com.tn.returnkey.returnorderapi.model.ReturnOrderResponse;
import com.tn.returnkey.returnorderapi.model.UpdateReturnOrderItemStatusResponse;
import com.tn.returnkey.returnorderapi.service.ReturnOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReturnOrderController {

    @Autowired
    private ReturnOrderService returnOrderService;

    @PostMapping("/pending/returns")
    public ResponseEntity<InitiateReturnOrderResponse> pendingReturnOrder(@RequestBody InitiateReturnOrderRequest request) {
        return new ResponseEntity<>(returnOrderService.initiateReturnOrder(request), HttpStatus.OK);
    }

    @PostMapping("/returns")
    public ResponseEntity<ReturnOrderResponse> createReturnOrders(@RequestBody ReturnOrderRequest returnOrderRequest) {
        return new ResponseEntity<>(returnOrderService.createReturnOrder(returnOrderRequest), HttpStatus.OK);
    }

    @GetMapping("/returns/{id}")
    public ResponseEntity<ReturnOrderResponse> getReturnOrder(@PathVariable Long id) {
        return new ResponseEntity<>(returnOrderService.getReturnOrder(id), HttpStatus.OK);
    }

    @PutMapping("/returns/{id}/items/{itemId}/qc/{status}")
    public ResponseEntity<UpdateReturnOrderItemStatusResponse> qc(@PathVariable Long id, @PathVariable String itemId, @PathVariable String status) {
        return new ResponseEntity<>(returnOrderService.updateItemStatus(id, itemId, status), HttpStatus.OK);
    }
}

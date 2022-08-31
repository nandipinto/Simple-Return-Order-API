package com.tn.returnkey.returnorderapi.model;

import com.tn.returnkey.returnorderapi.constants.ReturnOrderItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReturnOrderItem implements Serializable {
    private static final long serialVersionUID = -8565446787572177349L;

    private String orderId;
    private Long itemId;
    private String sku;
    private int quantity;
    private double price;
    private ReturnOrderItemStatus status;
}

package com.tn.returnkey.returnorderapi.model;

import com.tn.returnkey.returnorderapi.constants.ResponseMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateReturnOrderItemStatusResponse implements Serializable {

    private static final long serialVersionUID = 7259905018620286304L;

    private boolean successful;
    private ResponseMessage message;
    private Long returnOrderId;
    private ReturnOrderItem item;

}

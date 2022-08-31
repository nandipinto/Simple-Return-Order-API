package com.tn.returnkey.returnorderapi.model;

import com.tn.returnkey.returnorderapi.constants.ResponseMessage;
import com.tn.returnkey.returnorderapi.constants.ReturnOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnOrderResponse implements Serializable {

    private long returnOrderId;
    private ReturnOrderStatus status;
    private ReturnOrderResponseDetail detail;
    private boolean successful;
    private ResponseMessage message;
}

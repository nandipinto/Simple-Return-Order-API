package com.tn.returnkey.returnorderapi.model;

import com.tn.returnkey.returnorderapi.constants.ResponseMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InitiateReturnOrderResponse implements Serializable {

    private static final long serialVersionUID = -1552331422524513264L;

    private boolean successful;
    private ResponseMessage message;
    private String token;
}

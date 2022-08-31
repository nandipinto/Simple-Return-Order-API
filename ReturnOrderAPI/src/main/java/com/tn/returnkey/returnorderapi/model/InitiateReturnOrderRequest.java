package com.tn.returnkey.returnorderapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InitiateReturnOrderRequest implements Serializable {

    private static final long serialVersionUID = -2276590899690743620L;

    private String orderId;
    private String email;

}

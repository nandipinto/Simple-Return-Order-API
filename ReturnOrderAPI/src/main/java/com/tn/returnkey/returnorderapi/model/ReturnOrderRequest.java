package com.tn.returnkey.returnorderapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnOrderRequest implements Serializable {
    private static final long serialVersionUID = 6573950007623730540L;

    private String token;
    private List<ReturnOrderItem> items = new ArrayList<>();
}

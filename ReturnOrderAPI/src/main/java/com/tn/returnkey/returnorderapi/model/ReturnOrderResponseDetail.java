package com.tn.returnkey.returnorderapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnOrderResponseDetail implements Serializable {

    private static final long serialVersionUID = 1965226484294879135L;

    private double refundAmount;
    private List<ReturnOrderItem> items = new ArrayList<>();
}

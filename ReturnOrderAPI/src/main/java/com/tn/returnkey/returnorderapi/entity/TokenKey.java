package com.tn.returnkey.returnorderapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenKey implements Serializable {

    @Column(name = "order_id")
    private String orderId;
    @Column(name = "email")
    private String email;
}

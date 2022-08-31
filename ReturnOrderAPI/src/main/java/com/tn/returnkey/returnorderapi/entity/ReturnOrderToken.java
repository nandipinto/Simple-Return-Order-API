package com.tn.returnkey.returnorderapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "return_order_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnOrderToken extends BaseEntity{

    private static final long serialVersionUID = 6573590427724449820L;

    @EmbeddedId
    private TokenKey tokenKey;

    @Column(name = "token", unique = true, nullable = false)
    private String token;

}

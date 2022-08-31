package com.tn.returnkey.returnorderapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
@Entity
@Table(name = "orders", indexes = { @Index(columnList = "order_id, email") })
public class Order extends BaseEntity{

    private static final long serialVersionUID = -962178310633881333L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "order_id", length = 10, nullable = false)
    private String orderId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "sku", length = 10, nullable = false)
    private String sku;

    @Column(name = "qty")
    private int quantity;

    @Column(name = "price")
    private double price;

    @Column(name = "item_name", nullable = false)
    private String itemName;


}

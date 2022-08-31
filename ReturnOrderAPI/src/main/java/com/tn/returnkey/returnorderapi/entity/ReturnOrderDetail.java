package com.tn.returnkey.returnorderapi.entity;

import com.tn.returnkey.returnorderapi.constants.ReturnOrderItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "return_order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "returnOrder" })
@EqualsAndHashCode(exclude = { "returnOrder" })
@Builder
public class ReturnOrderDetail extends BaseEntity{
    private static final long serialVersionUID = -3294389552373864573L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "sku", length = 10, nullable = false)
    private String sku;

    @Column(name = "qty")
    private int quantity;

    @Column(name = "price")
    private double price;

    @Column(name = "status")
    private ReturnOrderItemStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_order_id", referencedColumnName = "id")
    private ReturnOrder returnOrder;
}

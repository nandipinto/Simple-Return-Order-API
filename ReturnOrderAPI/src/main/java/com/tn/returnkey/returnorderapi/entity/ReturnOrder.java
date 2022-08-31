package com.tn.returnkey.returnorderapi.entity;

import com.tn.returnkey.returnorderapi.constants.ReturnOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "return_orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = { "details" })
@Builder
public class ReturnOrder extends BaseEntity {
    private static final long serialVersionUID = 1428344527060193480L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "token")
    private String token;

    @Column(name = "order_id", length = 10, nullable = false)
    private String orderId;

    @Column(name = "status")
    private ReturnOrderStatus status;

    @OneToMany(mappedBy = "returnOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReturnOrderDetail> details = new ArrayList<>();

}

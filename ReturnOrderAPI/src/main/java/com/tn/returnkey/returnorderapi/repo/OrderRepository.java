package com.tn.returnkey.returnorderapi.repo;

import com.tn.returnkey.returnorderapi.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    boolean existsByOrderIdAndEmail(String orderId, String email);

    Long countByOrderIdAndEmail(String orderId, String email);

    List<Order> findByOrderIdAndEmail(String orderId, String email);

    Optional<Order> findOneByOrderIdAndSku(String orderId, String sku);
}

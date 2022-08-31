package com.tn.returnkey.returnorderapi.repo;

import com.tn.returnkey.returnorderapi.entity.ReturnOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReturnOrderRepository extends JpaRepository<ReturnOrder, Long> {

    boolean existsByToken(String token);

    List<ReturnOrder> findAllByToken(String token);

    Optional<ReturnOrder> findOneByTokenAndOrderId(String token, String orderId);
}

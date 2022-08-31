package com.tn.returnkey.returnorderapi.repo;

import com.tn.returnkey.returnorderapi.entity.ReturnOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReturnOrderDetailRepository extends JpaRepository<ReturnOrderDetail, Long> {

    Optional<ReturnOrderDetail> findOneByReturnOrderOrderIdAndSku(String orderId, String sku);
}

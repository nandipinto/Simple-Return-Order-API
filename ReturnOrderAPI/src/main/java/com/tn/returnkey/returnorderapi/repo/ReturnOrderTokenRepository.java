package com.tn.returnkey.returnorderapi.repo;

import com.tn.returnkey.returnorderapi.entity.ReturnOrderToken;
import com.tn.returnkey.returnorderapi.entity.TokenKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReturnOrderTokenRepository extends JpaRepository<ReturnOrderToken, TokenKey> {

    List<ReturnOrderToken> findByTokenKeyOrderId(String orderId);

    Optional<ReturnOrderToken> findOneByTokenKeyOrderIdAndToken(String orderId, String token);

    Optional<ReturnOrderToken> findOneByToken(String token);
}

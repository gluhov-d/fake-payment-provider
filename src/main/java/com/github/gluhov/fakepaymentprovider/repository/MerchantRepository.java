package com.github.gluhov.fakepaymentprovider.repository;

import com.github.gluhov.fakepaymentprovider.model.Merchant;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MerchantRepository extends R2dbcRepository<Merchant, UUID> {

    @Query("SELECT * FROM merchant m WHERE merchant_id = :1 AND secret_key = :2")
    Mono<Merchant> findByMerchantIdAndSecretKey(String merchantId, String secretKey);
}
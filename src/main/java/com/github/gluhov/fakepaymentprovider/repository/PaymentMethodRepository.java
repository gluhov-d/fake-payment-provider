package com.github.gluhov.fakepaymentprovider.repository;

import com.github.gluhov.fakepaymentprovider.model.PaymentMethod;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PaymentMethodRepository extends R2dbcRepository<PaymentMethod, UUID> {

    @Query("SELECT * FROM payment_method p WHERE p.type = :1")
    Mono<PaymentMethod> findPaymentMethodByType(String type);
}
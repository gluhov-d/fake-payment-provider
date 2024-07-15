package com.github.gluhov.fakepaymentprovider.repository;

import com.github.gluhov.fakepaymentprovider.model.Transaction;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface TransactionRepository extends R2dbcRepository<Transaction, UUID> {

    @Query("SELECT * FROM transaction t WHERE t.customer_id = :1 AND t.status = 'ACTIVE'")
    Flux<Transaction> getAllActiveByCustomerId(UUID id);

    @Query("SELECT * FROM transaction t WHERE t.merchant_id = :1 AND t.status = 'ACTIVE'")
    Flux<Transaction> getAllActiveByMerchantId(UUID id);
}

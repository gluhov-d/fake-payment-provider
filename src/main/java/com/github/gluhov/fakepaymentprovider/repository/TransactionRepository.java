package com.github.gluhov.fakepaymentprovider.repository;

import com.github.gluhov.fakepaymentprovider.model.Transaction;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TransactionRepository extends R2dbcRepository<Transaction, UUID> {

    @Query("SELECT * FROM transaction t WHERE t.merchant_id = :1 AND t.created_at >= :2 AND t.created_at <= :3")
    Flux<Transaction> getBetween(UUID merchantId, LocalDateTime startDate, LocalDateTime endDate);
}
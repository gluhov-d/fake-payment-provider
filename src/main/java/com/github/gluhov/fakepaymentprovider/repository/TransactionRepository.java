package com.github.gluhov.fakepaymentprovider.repository;

import com.github.gluhov.fakepaymentprovider.model.Transaction;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TransactionRepository extends R2dbcRepository<Transaction, UUID> {

    @Query("SELECT * FROM transaction t WHERE t.merchant_id = :1 AND t.type = :2 AND t.created_at >= :3 AND t.created_at <= :4")
    Flux<Transaction> getBetweenByType(UUID merchantId, String type, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT * FROM transaction t WHERE t.id = :1 AND t.type = :2")
    Mono<Transaction> getByIdAndType(UUID uuid, String type);
}
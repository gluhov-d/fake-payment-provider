package com.github.gluhov.fakepaymentprovider.repository;

import com.github.gluhov.fakepaymentprovider.model.CardData;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CardRepository extends R2dbcRepository<CardData, UUID> {

    @Query("SELECT * FROM card_data c WHERE c.account_id = :1 AND c.card_number = :2")
    Mono<CardData> findCardByAccountIdAAndCardNumber(UUID accountId, String cardNumber);
}
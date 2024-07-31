package com.github.gluhov.fakepaymentprovider.repository;

import com.github.gluhov.fakepaymentprovider.model.Account;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AccountRepository extends R2dbcRepository<Account, UUID> {
    @Query("SELECT * FROM account a WHERE a.owner_id = :1 AND a.owner_type = :2")
    Mono<Account> findByOwnerIdAndType(UUID uuid, String type);
}
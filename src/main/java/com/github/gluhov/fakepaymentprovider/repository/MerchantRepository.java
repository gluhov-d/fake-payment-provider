package com.github.gluhov.fakepaymentprovider.repository;

import com.github.gluhov.fakepaymentprovider.model.Merchant;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface MerchantRepository extends R2dbcRepository<Merchant, UUID> {
}

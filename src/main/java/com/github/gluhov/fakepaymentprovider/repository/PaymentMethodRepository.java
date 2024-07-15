package com.github.gluhov.fakepaymentprovider.repository;

import com.github.gluhov.fakepaymentprovider.model.PaymentMethod;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface PaymentMethodRepository extends R2dbcRepository<PaymentMethod, UUID> {
}

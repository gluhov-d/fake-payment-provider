package com.github.gluhov.fakepaymentprovider.repository;

import com.github.gluhov.fakepaymentprovider.model.Customer;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface CustomerRepository extends R2dbcRepository<Customer, UUID> {
}
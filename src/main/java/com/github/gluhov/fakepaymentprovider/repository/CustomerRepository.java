package com.github.gluhov.fakepaymentprovider.repository;

import com.github.gluhov.fakepaymentprovider.model.Customer;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CustomerRepository extends R2dbcRepository<Customer, UUID> {

    @Query("SELECT * FROM customer c WHERE  c.first_name = :1 AND c.last_name = :2")
    Mono<Customer> findCustomerByFirstNameAndLastName(String firstName, String lastName);
}
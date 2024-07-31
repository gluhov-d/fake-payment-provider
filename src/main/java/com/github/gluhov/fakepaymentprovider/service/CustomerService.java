package com.github.gluhov.fakepaymentprovider.service;

import com.github.gluhov.fakepaymentprovider.exception.EntityNotFoundException;
import com.github.gluhov.fakepaymentprovider.model.*;
import com.github.gluhov.fakepaymentprovider.repository.AccountRepository;
import com.github.gluhov.fakepaymentprovider.repository.CardRepository;
import com.github.gluhov.fakepaymentprovider.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;

    public Mono<Customer> getById(UUID uuid) {
        return customerRepository.findById(uuid)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Customer not found exception", "FPP_CUSTOMER_NOT_FOUND")));
    }

    public Mono<Customer> findCustomerByFirstNameAndLastNameAndCountry(String firstName, String lastName, String country) {
        return customerRepository.findCustomerByFirstNameAndLastNameAndCountry(firstName, lastName, country);
    }

    public Mono<Customer> createNewCustomer(CardData cardData, Customer customer, Transaction transaction, UUID uuid, LocalDateTime now) {
        return accountRepository.save(
                        Account.builder()
                                .balance(0L)
                                .ownerId(uuid)
                                .ownerType("customer")
                                .currency(transaction.getCurrency())
                                .createdAt(now)
                                .updatedAt(now)
                                .status(Status.ACTIVE)
                                .createdBy(String.valueOf(uuid))
                                .modifiedBy(String.valueOf(uuid))
                                .build())
                .flatMap(savedAccount -> cardRepository.save(
                                CardData.builder()
                                        .cvv(cardData.getCvv())
                                        .expDate(cardData.getExpDate())
                                        .cardNumber(cardData.getCardNumber())
                                        .account_id(savedAccount.getId())
                                        .createdAt(now)
                                        .updatedAt(now)
                                        .createdBy(String.valueOf(uuid))
                                        .modifiedBy(uuid.toString())
                                        .status(Status.ACTIVE)
                                        .build())
                        .flatMap(savedCard -> customerRepository.save(
                                        Customer.builder()
                                                .firstName(customer.getFirstName())
                                                .lastName(customer.getLastName())
                                                .country(customer.getCountry())
                                                .accountId(savedAccount.getId())
                                                .status(Status.ACTIVE)
                                                .createdAt(now)
                                                .updatedAt(now)
                                                .createdBy(String.valueOf(uuid))
                                                .modifiedBy(String.valueOf(uuid))
                                                .build())
                        )
                );
    }
}
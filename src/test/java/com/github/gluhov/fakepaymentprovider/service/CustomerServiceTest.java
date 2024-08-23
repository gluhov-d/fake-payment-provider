package com.github.gluhov.fakepaymentprovider.service;

import com.github.gluhov.fakepaymentprovider.exception.EntityNotFoundException;
import com.github.gluhov.fakepaymentprovider.model.Customer;
import com.github.gluhov.fakepaymentprovider.repository.AccountRepository;
import com.github.gluhov.fakepaymentprovider.repository.CardRepository;
import com.github.gluhov.fakepaymentprovider.repository.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import static com.github.gluhov.fakepaymentprovider.service.AccountData.accountCustomer;
import static com.github.gluhov.fakepaymentprovider.service.CardTestData.cardCustomerData;
import static com.github.gluhov.fakepaymentprovider.service.CustomerData.*;
import static com.github.gluhov.fakepaymentprovider.service.TransactionData.transaction;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class CustomerServiceTest {
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    @DisplayName("Test get by id functionality")
    void getById() {
        when(customerRepository.findById(CUSTOMER_UUID)).thenReturn(Mono.just(customerData));

        Mono<Customer> result = customerService.getById(CUSTOMER_UUID);
        StepVerifier.create(result)
                        .expectNext(customerData)
                                .verifyComplete();
        verify(customerRepository, times(1)).findById(CUSTOMER_UUID);
    }

    @Test
    @DisplayName("Test get by not found id functionality")
    void getNotFound() {
        when(customerRepository.findById(CUSTOMER_NOT_FOUND_UUID)).thenReturn(Mono.empty());

        Mono<Customer> result = customerService.getById(CUSTOMER_NOT_FOUND_UUID);
        StepVerifier.create(result)
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof EntityNotFoundException);
                    assertEquals("Customer not found exception", error.getMessage());
                    assertEquals("FPP_CUSTOMER_NOT_FOUND", ((EntityNotFoundException) error).getErrorCode());
                }).verify();
    }

    @Test
    @DisplayName("Test find by owner id and type functionality")
    void findByOwnerIdAndType() {
        when(customerRepository.findCustomerByFirstNameAndLastNameAndCountry(customerData.getFirstName(), customerData.getLastName(),
                customerData.getCountry())).thenReturn(Mono.just(customerData));

        Mono<Customer> result = customerService.findCustomerByFirstNameAndLastNameAndCountry(customerData.getFirstName(),
                customerData.getLastName(), customerData.getCountry());
        StepVerifier.create(result)
                        .expectNext(customerData)
                                .verifyComplete();
        verify(customerRepository, times(1)).findCustomerByFirstNameAndLastNameAndCountry(customerData.getFirstName(),
                customerData.getLastName(), customerData.getCountry());
    }

    @Test
    @DisplayName("Test create new customer functionality")
    void createNewCustomer() {
        when(accountRepository.save(any())).thenReturn(Mono.just(accountCustomer));
        when(cardRepository.save(any())).thenReturn(Mono.just(cardCustomerData));
        when(customerRepository.save(any())).thenReturn(Mono.just(customerData));

        Mono<Customer> result = customerService.createNewCustomer(cardCustomerData, customerData, transaction, UUID.fromString("4c09f680-1c67-11ec-9621-0242ac130002"), LocalDateTime.now());
        accountCustomer.setCardsData(new HashSet<>(Arrays.asList(cardCustomerData)));
        customerData.setAccount(accountCustomer);
        StepVerifier.create(result)
                        .expectNext(customerData)
                                .verifyComplete();
        verify(customerRepository, times(1)).save(any());
        verify(cardRepository, times(1)).save(any());
        verify(accountRepository, times(1)).save(any());
    }
}
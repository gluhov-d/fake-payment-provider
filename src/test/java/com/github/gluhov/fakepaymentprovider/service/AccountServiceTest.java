package com.github.gluhov.fakepaymentprovider.service;

import com.github.gluhov.fakepaymentprovider.exception.EntityNotFoundException;
import com.github.gluhov.fakepaymentprovider.model.Account;
import com.github.gluhov.fakepaymentprovider.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.github.gluhov.fakepaymentprovider.service.AccountData.*;
import static com.github.gluhov.fakepaymentprovider.service.TransactionData.transaction;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    @DisplayName("Test get by id info functionality")
    void getById() {
        when(accountRepository.findById(ACCOUNT_CUSTOMER_UUID)).thenReturn(Mono.just(accountCustomer));

        Mono<Account> result = accountService.getById(ACCOUNT_CUSTOMER_UUID);
        StepVerifier.create(result)
                        .expectNext(accountCustomer)
                                .verifyComplete();
        verify(accountRepository, times(1)).findById(ACCOUNT_CUSTOMER_UUID);
    }

    @Test
    @DisplayName("Test get by not found id info functionality")
    void getNotFound() {
        when(accountRepository.findById(ACCOUNT_NOT_FOUND_UUID)).thenReturn(Mono.empty());

        Mono<Account> result = accountService.getById(ACCOUNT_NOT_FOUND_UUID);
        StepVerifier.create(result)
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof EntityNotFoundException);
                    assertEquals("Account not found", error.getMessage());
                    assertEquals("FPP_ACCOUNT_NOT_FOUND", ((EntityNotFoundException) error).getErrorCode());
                }).verify();
        verify(accountRepository, times(1)).findById(ACCOUNT_NOT_FOUND_UUID);
    }

    @Test
    @DisplayName("Test find by owner id and type functionality")
    void findByOwnerIdAndType() {
        when(accountRepository.findByOwnerIdAndType(accountCustomer.getOwnerId(), accountCustomer.getOwnerType())).thenReturn(Mono.just(accountCustomer));

        Mono<Account> result = accountService.findByOwnerIdAndType(accountCustomer.getOwnerId(), accountCustomer.getOwnerType());
        StepVerifier.create(result)
                        .expectNext(accountCustomer)
                                .verifyComplete();
        verify(accountRepository, times(1)).findByOwnerIdAndType(accountCustomer.getOwnerId(), accountCustomer.getOwnerType());
    }

    @Test
    @DisplayName("Test make money transfer functionality")
    void makeMoneyTransfer() {
        when(accountRepository.findByOwnerIdAndType(accountCustomer.getOwnerId(), accountCustomer.getOwnerType())).thenReturn(Mono.just(accountCustomer));
        when(accountRepository.findByOwnerIdAndType(accountMerchant.getOwnerId(), accountMerchant.getOwnerType())).thenReturn(Mono.just(accountMerchant));
        when(accountRepository.save(accountCustomer)).thenReturn(Mono.just(accountUpdatedCustomer));
        when(accountRepository.save(accountMerchant)).thenReturn(Mono.just(accountUpdatedMerchant));

        StepVerifier.create(accountService.makeMoneyTransfer(transaction))
                        .thenAwait()
                                .verifyComplete();

        verify(accountRepository, times(1)).findByOwnerIdAndType(accountCustomer.getOwnerId(), accountCustomer.getOwnerType());
        verify(accountRepository, times(1)).save(accountUpdatedCustomer);
        verify(accountRepository, times(1)).findByOwnerIdAndType(accountMerchant.getOwnerId(), accountMerchant.getOwnerType());
        verify(accountRepository, times(1)).save(accountUpdatedMerchant);
    }
}
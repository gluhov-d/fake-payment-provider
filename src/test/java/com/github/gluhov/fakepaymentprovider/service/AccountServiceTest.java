package com.github.gluhov.fakepaymentprovider.service;

import com.github.gluhov.fakepaymentprovider.exception.EntityNotFoundException;
import com.github.gluhov.fakepaymentprovider.model.Account;
import com.github.gluhov.fakepaymentprovider.model.Transaction;
import com.github.gluhov.fakepaymentprovider.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.github.gluhov.fakepaymentprovider.service.AccountData.*;
import static com.github.gluhov.fakepaymentprovider.service.TransactionData.transaction;
import static org.assertj.core.api.AssertionsForClassTypes.fail;
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
    void getById() {
        when(accountRepository.findById(ACCOUNT_CUSTOMER_UUID)).thenReturn(Mono.just(accountCustomer));

        Mono<Account> result = accountService.getById(ACCOUNT_CUSTOMER_UUID);
        assertEquals(accountCustomer, result.block());
        verify(accountRepository, times(1)).findById(ACCOUNT_CUSTOMER_UUID);
    }

    @Test
    void getNotFound() {
        when(accountRepository.findById(ACCOUNT_NOT_FOUND_UUID)).thenReturn(Mono.empty());

        accountService.getById(ACCOUNT_NOT_FOUND_UUID).subscribe(entity -> fail("Expected an EntityNotFoundException to be throw"),
                error -> {
                    assertTrue(error instanceof EntityNotFoundException);
                    assertEquals("Account not found", error.getMessage());
                    assertEquals("FPP_ACCOUNT_NOT_FOUND", ((EntityNotFoundException) error).getErrorCode());
                });
        verify(accountRepository, times(1)).findById(ACCOUNT_NOT_FOUND_UUID);
    }

    @Test
    void findByOwnerIdAndType() {
        when(accountRepository.findByOwnerIdAndType(accountCustomer.getOwnerId(), accountCustomer.getOwnerType())).thenReturn(Mono.just(accountCustomer));

        Mono<Account> result = accountService.findByOwnerIdAndType(accountCustomer.getOwnerId(), accountCustomer.getOwnerType());
        assertEquals(accountCustomer, result.block());
        verify(accountRepository, times(1)).findByOwnerIdAndType(accountCustomer.getOwnerId(), accountCustomer.getOwnerType());
    }

    @Test
    void makeMoneyTransfer() {
        when(accountRepository.findByOwnerIdAndType(accountCustomer.getOwnerId(), accountCustomer.getOwnerType())).thenReturn(Mono.just(accountCustomer));
        when(accountRepository.findByOwnerIdAndType(accountMerchant.getOwnerId(), accountMerchant.getOwnerType())).thenReturn(Mono.just(accountMerchant));
        /*when(accountRepository.findByOwnerIdAndType(accountCustomer.getOwnerId(), accountCustomer.getOwnerType())).thenReturn(Mono.just(accountCustomer));
        when(accountRepository.findByOwnerIdAndType(accountMerchant.getOwnerId(), accountMerchant.getOwnerType())).thenReturn(Mono.just(accountMerchant));*/
        when(accountRepository.save(accountCustomer)).thenReturn(Mono.just(accountUpdatedCustomer));
        when(accountRepository.save(accountMerchant)).thenReturn(Mono.just(accountUpdatedMerchant));

        accountService.makeMoneyTransfer(transaction).block();

        verify(accountRepository, times(1)).findByOwnerIdAndType(accountCustomer.getOwnerId(), accountCustomer.getOwnerType());
        verify(accountRepository, times(1)).save(accountUpdatedCustomer);
        verify(accountRepository, times(1)).findByOwnerIdAndType(accountMerchant.getOwnerId(), accountMerchant.getOwnerType());
        verify(accountRepository, times(1)).save(accountUpdatedMerchant);
    }

    @Test
    void makeMoneyTransfer1() {
        UUID customerOwnerId = UUID.randomUUID();
        String customerOwnerType = "customer";
        UUID merchantOwnerId = UUID.randomUUID();
        String merchantOwnerType = "merchant";

        Account accountCustomer = Account.builder()
                .id(UUID.randomUUID())
                .balance(1000L)
                .ownerId(customerOwnerId)
                .ownerType(customerOwnerType)
                .build();

        Account accountMerchant = Account.builder()
                .id(UUID.randomUUID())
                .balance(500L)
                .ownerId(merchantOwnerId)
                .ownerType(merchantOwnerType)
                .build();

        Account accountUpdatedCustomer = Account.builder()
                .id(accountCustomer.getId())
                .balance(accountCustomer.getBalance() - 200L)  // Assume transfer amount is 200
                .ownerId(customerOwnerId)
                .ownerType(customerOwnerType)
                .build();

        Account accountUpdatedMerchant = Account.builder()
                .id(accountMerchant.getId())
                .balance(accountMerchant.getBalance() + 200L)  // Assume transfer amount is 200
                .ownerId(merchantOwnerId)
                .ownerType(merchantOwnerType)
                .build();

        Transaction transaction = new Transaction();
        transaction.setAmount(200L);
        transaction.setCurrency("USD");
        transaction.setType("transaction");
        transaction.setCustomerId(customerOwnerId);
        transaction.setMerchantId(merchantOwnerId);

        when(accountRepository.findByOwnerIdAndType(customerOwnerId, customerOwnerType)).thenReturn(Mono.just(accountCustomer));
        when(accountRepository.findByOwnerIdAndType(merchantOwnerId, merchantOwnerType)).thenReturn(Mono.just(accountMerchant));
        when(accountRepository.save(accountCustomer)).thenReturn(Mono.just(accountUpdatedCustomer));
        when(accountRepository.save(accountMerchant)).thenReturn(Mono.just(accountUpdatedMerchant));

        accountService.makeMoneyTransfer(transaction).block();  // Ensure the operation completes

        verify(accountRepository, times(1)).findByOwnerIdAndType(customerOwnerId, customerOwnerType);
        verify(accountRepository, times(1)).save(accountUpdatedCustomer);
        verify(accountRepository, times(1)).findByOwnerIdAndType(merchantOwnerId, merchantOwnerType);
        verify(accountRepository, times(1)).save(accountUpdatedMerchant);
    }
}
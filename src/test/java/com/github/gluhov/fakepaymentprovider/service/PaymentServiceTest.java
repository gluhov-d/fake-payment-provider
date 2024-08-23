package com.github.gluhov.fakepaymentprovider.service;

import com.github.gluhov.fakepaymentprovider.dto.*;
import com.github.gluhov.fakepaymentprovider.exception.EntityNotFoundException;
import com.github.gluhov.fakepaymentprovider.mapper.CardDataMapper;
import com.github.gluhov.fakepaymentprovider.mapper.CustomerMapper;
import com.github.gluhov.fakepaymentprovider.mapper.PaymentMethodMapper;
import com.github.gluhov.fakepaymentprovider.mapper.TransactionMapper;
import com.github.gluhov.fakepaymentprovider.model.Transaction;
import com.github.gluhov.fakepaymentprovider.repository.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static com.github.gluhov.fakepaymentprovider.service.AccountData.accountCustomer;
import static com.github.gluhov.fakepaymentprovider.service.CardTestData.cardCustomerData;
import static com.github.gluhov.fakepaymentprovider.service.CardTestData.cardCustomerDataDto;
import static com.github.gluhov.fakepaymentprovider.service.CustomerData.customerData;
import static com.github.gluhov.fakepaymentprovider.service.CustomerData.customerDataDto;
import static com.github.gluhov.fakepaymentprovider.service.MerchantData.MERCHANT_UUID;
import static com.github.gluhov.fakepaymentprovider.service.MerchantData.merchant;
import static com.github.gluhov.fakepaymentprovider.service.PaymentMethodData.paymentMethod;
import static com.github.gluhov.fakepaymentprovider.service.PaymentMethodData.paymentMethodDto;
import static com.github.gluhov.fakepaymentprovider.service.TransactionData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class PaymentServiceTest {

    @Mock
    private AccountService accountService;
    @Mock
    private MerchantService merchantService;
    @Mock
    private CardService cardService;
    @Mock
    private CustomerService customerService;
    @Mock
    private PaymentMethodService paymentMethodService;
    @Mock
    private WebhookService webhookService;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private TransactionMapper transactionMapper;
    @Mock
    private CustomerMapper customerMapper;
    @Mock
    private CardDataMapper cardDataMapper;
    @Mock
    private PaymentMethodMapper paymentMethodMapper;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    @DisplayName("Test get between by type functionality")
    void getBetweenByType() {
        when(transactionRepository.getBetweenByType(MERCHANT_UUID, transaction.getType(), START_DATE, END_DATE)).thenReturn(Flux.just(transaction));

        when(accountService.getById(accountCustomer.getId())).thenReturn(Mono.just(accountCustomer));
        when(customerService.getById(transaction.getCustomerId())).thenReturn(Mono.just(customerData));
        when(cardService.getById(transaction.getCardId())).thenReturn(Mono.just(cardCustomerData));
        when(paymentMethodService.getById(transaction.getPaymentMethodId())).thenReturn(Mono.just(paymentMethod));

        when(customerMapper.map(customerData)).thenReturn(new CustomerDto(customerData));
        when(transactionMapper.map(transaction)).thenReturn(transactionDto);
        when(cardDataMapper.map(cardCustomerData)).thenReturn(new CardDataDto(cardCustomerData));
        when(paymentMethodMapper.map(paymentMethod)).thenReturn(new PaymentMethodDto(paymentMethod));

        Flux<TransactionDto> result = paymentService.getBetweenByType(START_DATE, END_DATE, MERCHANT_UUID, transaction.getType());
        StepVerifier.create(result)
                        .assertNext(r -> {
                            assertEquals(transaction.getId(), r.getTransactionId());
                        })
                .verifyComplete();
    }

    @Test
    @DisplayName("Test get by id and type functionality")
    void getByIdAndType() {
        when(transactionRepository.getByIdAndType(transaction.getId(), transaction.getType())).thenReturn(Mono.just(transaction));
        when(accountService.getById(accountCustomer.getId())).thenReturn(Mono.just(accountCustomer));
        when(customerService.getById(transaction.getCustomerId())).thenReturn(Mono.just(customerData));
        when(cardService.getById(transaction.getCardId())).thenReturn(Mono.just(cardCustomerData));
        when(paymentMethodService.getById(transaction.getPaymentMethodId())).thenReturn(Mono.just(paymentMethod));

        when(customerMapper.map(customerData)).thenReturn(customerDataDto);
        when(transactionMapper.map(transaction)).thenReturn(transactionDto);
        when(cardDataMapper.map(cardCustomerData)).thenReturn(cardCustomerDataDto);
        when(paymentMethodMapper.map(paymentMethod)).thenReturn(paymentMethodDto);

        Mono<TransactionDto> result = paymentService.getByIdAndType(transaction.getId(), transaction.getType());
        StepVerifier.create(result)
                        .expectNext(transactionDto)
                                .verifyComplete();
    }

    @Test
    @DisplayName("Test get by and type not found functionality")
    void getByIdAndTypeNotFound() {
        when(transactionRepository.getByIdAndType(any(), any())).thenReturn(Mono.empty());

        Mono<TransactionDto> result = paymentService.getByIdAndType(TRANSACTION_NOT_FOUND_UUID, transaction.getType());
        StepVerifier.create(result)
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof EntityNotFoundException);
                    assertEquals("Transaction not found", error.getMessage());
                    assertEquals("FPP_TRANSACTION_NOT_FOUND", ((EntityNotFoundException) error).getErrorCode());
                }).verify();
    }

    @Test
    @DisplayName("Test get all with status IN_PROGRESS functionality")
    void getAllWithStatusInProgress() {
        when(transactionRepository.getAllWithStatusInProgress()).thenReturn(Flux.fromIterable(transactionsInProgress));

        Flux<Transaction> result = paymentService.getAllWithStatusInProgress();
        StepVerifier.create(result.collectList())
                        .expectNext(transactionsInProgress)
                                .verifyComplete();
    }

    @Test
    void createTransaction() {
        when(transactionRepository.save(transaction)).thenReturn(Mono.just(transaction));

        when(transactionMapper.map(transactionDto)).thenReturn(transaction);
        when(cardDataMapper.map(transactionDto.getCardData())).thenReturn(cardCustomerData);
        when(customerMapper.map(transactionDto.getCustomer())).thenReturn(customerData);
        when(paymentMethodMapper.map(transactionDto.getPaymentMethod())).thenReturn(paymentMethod);

        when(merchantService.getById(MERCHANT_UUID)).thenReturn(Mono.just(merchant));
        when(paymentMethodService.findByType(paymentMethod.getType())).thenReturn(Mono.just(paymentMethod));
        when(customerService.findCustomerByFirstNameAndLastNameAndCountry(customerData.getFirstName(), customerData.getLastName(), customerData.getCountry())).thenReturn(Mono.just(customerData));
        when(customerService.createNewCustomer(cardCustomerData, customerData, transaction, transaction.getMerchantId(), LocalDateTime.now())).thenReturn(Mono.just(customerData));
        when(accountService.getById(customerData.getAccountId())).thenReturn(Mono.just(accountCustomer));
        when(cardService.findByCardAccountIdAndCardNumber(accountCustomer.getId(), cardCustomerData.getCardNumber())).thenReturn(Mono.just(cardCustomerData));
        when(cardService.save(cardCustomerData)).thenReturn(Mono.just(cardCustomerData));
        when(webhookService.sendNotification(transaction)).thenReturn(Mono.empty());

        Mono<TransactionResponseDto> result = paymentService.createTransaction(transactionDto, transaction.getMerchantId(), transaction.getType());
        StepVerifier.create(result)
                        .assertNext(r -> {
                            assertEquals(transaction.getId(), r.getTransactionId());
                            assertEquals(transaction.getTransactionStatus(), r.getStatus());
                        })
                .verifyComplete();
    }
}
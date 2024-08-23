package com.github.gluhov.fakepaymentprovider.rest;

import com.github.gluhov.fakepaymentprovider.dto.TransactionDto;
import com.github.gluhov.fakepaymentprovider.dto.TransactionDtoListResponse;
import com.github.gluhov.fakepaymentprovider.dto.TransactionResponseDto;
import com.github.gluhov.fakepaymentprovider.exception.ApiException;
import com.github.gluhov.fakepaymentprovider.exception.EntityNotFoundException;
import com.github.gluhov.fakepaymentprovider.security.CustomPrincipal;
import com.github.gluhov.fakepaymentprovider.service.PaymentService;
import com.github.gluhov.fakepaymentprovider.util.DateTimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static com.github.gluhov.fakepaymentprovider.service.MerchantData.merchant;
import static com.github.gluhov.fakepaymentprovider.service.TransactionData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class PaymentTopUpRestControllerV1Test {
    @Mock
    private Authentication authentication;
    @Mock
    private CustomPrincipal customPrincipal;
    @Mock
    private PaymentService paymentService;
    @InjectMocks
    private PaymentTopUpRestControllerV1 paymentTopUpRestControllerV1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test get by id topup info functionality")
    void givenId_whenGetById_thenSuccessResponse() {
        when(paymentService.getByIdAndType(transaction.getId(), transaction.getType()))
                .thenReturn(Mono.just(transactionDto));
        Mono<ResponseEntity<TransactionDto>> result = (Mono<ResponseEntity<TransactionDto>>) paymentTopUpRestControllerV1.getById(transaction.getId());
        StepVerifier.create(result)
                .assertNext(r -> {
                    assertNotNull(r);
                    assert transactionDto.getTransactionId().equals(r.getBody().getTransactionId());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Test get by not found id topup info functionality")
    void givenNotFoundId_whenGetById_thenNotFoundError() {
        when(paymentService.getByIdAndType(TRANSACTION_NOT_FOUND_UUID, transaction.getType()))
                .thenReturn(Mono.error(new EntityNotFoundException("Transaction not found", "FPP_TRANSACTION_NOT_FOUND")));
        Mono<?> result = paymentTopUpRestControllerV1.getById(TRANSACTION_NOT_FOUND_UUID);
        StepVerifier.create(result)
                .expectErrorSatisfies(e -> {
                    assert e instanceof EntityNotFoundException;
                    EntityNotFoundException entityNotFoundException = (EntityNotFoundException) e;
                    assert  "Transaction not found".equals(entityNotFoundException.getMessage());
                    assert "FPP_TRANSACTION_NOT_FOUND".equals(entityNotFoundException.getErrorCode());
                })
                .verify();
    }

    @Test
    @DisplayName("Test get between topup one found functionality")
    void givenDayBefore_whenGetBetween_thenOneFound() {
        when(authentication.getPrincipal()).thenReturn(customPrincipal);
        when(customPrincipal.getUuid()).thenReturn(merchant.getId());
        when(paymentService.getBetweenByType(DateTimeUtil.dayOrMin(START_TIME_DATE_BEFORE), DateTimeUtil.dayOrMax(END_TIME_DATE_BEFORE), merchant.getId(), transaction.getType()))
                .thenReturn(Flux.fromIterable(Arrays.asList(transactionDto)));

        Mono<ResponseEntity<TransactionDtoListResponse>> result = (Mono<ResponseEntity<TransactionDtoListResponse>>) paymentTopUpRestControllerV1.getBetween(START_TIME_DATE_BEFORE, END_TIME_DATE_BEFORE, authentication);

        StepVerifier.create(result)
                .assertNext(r -> {
                    assertNotNull(r);
                    List<TransactionDto> transactionDtos = (List<TransactionDto>) r.getBody().getTransactionList();
                    assertNotNull(transactionDtos);
                    assertEquals(1, transactionDtos.size());

                    assertEquals(transactionDto.getTransactionId(), transactionDtos.get(0).getTransactionId());
                    assertEquals(transactionDto.getAmount(), transactionDtos.get(0).getAmount());
                    assertEquals(transactionDto.getCurrency(), transactionDtos.get(0).getCurrency());
                    assertEquals(transactionDto.getNotificationUrl(), transactionDtos.get(0).getNotificationUrl());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Test get between topup three days before nothing found functionality")
    void givenThreeDaysBefore_whenGetBetween_thenNothingFound() {
        when(authentication.getPrincipal()).thenReturn(customPrincipal);
        when(customPrincipal.getUuid()).thenReturn(merchant.getId());
        when(paymentService.getBetweenByType(DateTimeUtil.dayOrMin(START_TIME_DATE_THREE_DAYS_BEFORE), DateTimeUtil.dayOrMax(END_TIME_DATE_THREE_DAYS_BEFORE), merchant.getId(), transaction.getType()))
                .thenReturn(Flux.empty());

        Mono<ResponseEntity<TransactionDtoListResponse>> result = (Mono<ResponseEntity<TransactionDtoListResponse>>) paymentTopUpRestControllerV1.getBetween(START_TIME_DATE_THREE_DAYS_BEFORE, END_TIME_DATE_THREE_DAYS_BEFORE, authentication);

        StepVerifier.create(result)
                .assertNext(r -> {
                    assertNotNull(r);
                    List<TransactionDto> transactionDtos = (List<TransactionDto>) r.getBody().getTransactionList();
                    assertNotNull(transactionDtos);
                    assertEquals(0, transactionDtos.size());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Test create topup transaction then success")
    void givenTopupTransaction_whenTopup_thenSuccessResponse() {
        when(authentication.getPrincipal()).thenReturn(customPrincipal);
        when(customPrincipal.getUuid()).thenReturn(merchant.getId());
        when(paymentService.createTransaction(transactionDto, merchant.getId(), transaction.getType())).thenReturn(Mono.just(new TransactionResponseDto(transaction.getId(), "OK", transaction.getTransactionStatus())));

        Mono<ResponseEntity<TransactionResponseDto>> result = (Mono<ResponseEntity<TransactionResponseDto>>) paymentTopUpRestControllerV1.topUp(transactionDto, authentication);

        StepVerifier.create(result)
                .assertNext(r -> {
                    assertNotNull(r);
                    TransactionResponseDto transactionResponseDto = r.getBody();
                    assertNotNull(transactionResponseDto);
                    assertEquals(transactionResponseDto.getTransactionId(), transaction.getId());
                    assertEquals(transactionResponseDto.getStatus(), transaction.getTransactionStatus());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Test create topup transaction then error Customer not found")
    void givenTopupTransaction_whenTopup_thenErrorResponse() {
        when(authentication.getPrincipal()).thenReturn(customPrincipal);
        when(customPrincipal.getUuid()).thenReturn(merchant.getId());
        when(paymentService.createTransaction(transactionDto, merchant.getId(), transaction.getType())).thenReturn(Mono.error(new ApiException("Customer not found", "FPP_CUSTOMER_NOT_FOUND")));

        Mono<?> result = paymentTopUpRestControllerV1.topUp(transactionDto, authentication);

        StepVerifier.create(result)
                .expectErrorSatisfies(error -> {
                    assert error instanceof ApiException;
                    ApiException apiException = (ApiException) error;
                    assert "Customer not found".equals(apiException.getMessage());
                    assert "FPP_CUSTOMER_NOT_FOUND".equals(apiException.getErrorCode());
                })
                .verify();
    }
}
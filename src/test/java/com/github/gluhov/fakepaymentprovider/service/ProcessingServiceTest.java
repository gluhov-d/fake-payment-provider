package com.github.gluhov.fakepaymentprovider.service;

import com.github.gluhov.fakepaymentprovider.exception.ProcessingException;
import com.github.gluhov.fakepaymentprovider.model.Transaction;
import com.github.gluhov.fakepaymentprovider.model.TransactionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.util.UUID;

import static com.github.gluhov.fakepaymentprovider.service.TransactionData.transaction;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProcessingServiceTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private WebhookService webhookService;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private ProcessingService processingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test change transaction status when status SUCCESS functionality")
    public void testChangeTransactionStatus_Success() {
        VirtualTimeScheduler.getOrSet();

        Transaction successTransaction = Transaction.builder()
                .transactionStatus(TransactionStatus.SUCCESS)
                .customerId(UUID.randomUUID())
                .merchantId(UUID.randomUUID())
                .type("transaction")
                .amount(100L)
                .build();

        when(paymentService.getAllWithStatusInProgress()).thenReturn(Flux.just(transaction));
        when(paymentService.updateTransactionStatus(any(), any())).thenReturn(Mono.just(successTransaction));
        when(accountService.makeMoneyTransfer(any())).thenReturn(Mono.empty());
        when(webhookService.sendNotification(any())).thenReturn(Mono.empty());

        StepVerifier.withVirtualTime(() -> processingService.changeTransactionStatus())
                .thenAwait()
                .verifyComplete();

        verify(paymentService, times(1)).getAllWithStatusInProgress();
        verify(paymentService, times(1)).updateTransactionStatus(eq(transaction.getId()), any(TransactionStatus.class));
        verify(accountService, times(1)).makeMoneyTransfer(successTransaction);
        verify(webhookService, times(1)).sendNotification(successTransaction);
    }

    @Test
    @DisplayName("Test change transaction status when status FAILED functionality")
    public void testChangeTransactionStatus_Failure() {
        VirtualTimeScheduler.getOrSet();
        Transaction failureTransaction = Transaction.builder()
                .transactionStatus(TransactionStatus.FAILED)
                .build();
        when(paymentService.getAllWithStatusInProgress()).thenReturn(Flux.just(transaction));
        when(paymentService.updateTransactionStatus(any(), any())).thenReturn(Mono.just(failureTransaction));
        when(webhookService.sendNotification(any())).thenReturn(Mono.empty());


        StepVerifier.withVirtualTime(() -> processingService.changeTransactionStatus())
                .thenAwait()
                .verifyComplete();

        verify(paymentService, times(1)).getAllWithStatusInProgress();
        verify(paymentService, times(1)).updateTransactionStatus(eq(transaction.getId()), any(TransactionStatus.class));
        verify(accountService, never()).makeMoneyTransfer(failureTransaction);
        verify(webhookService, times(1)).sendNotification(failureTransaction);
    }

    @Test
    @DisplayName("Test change transaction status when processing exception functionality")
    public void testChangeTransactionStatus_Error() {
        VirtualTimeScheduler.getOrSet();

        when(paymentService.getAllWithStatusInProgress()).thenReturn(Flux.just(transaction));
        when(paymentService.updateTransactionStatus(any(), any())).thenReturn(Mono.error(new ProcessingException("Transaction not updated", "FPP_PROCESSING_ERROR")));

        StepVerifier.withVirtualTime(() -> processingService.changeTransactionStatus())
                .expectErrorMatches(throwable -> throwable instanceof ProcessingException &&
                        throwable.getMessage().equals("Transaction not updated"))
                .verify();

        verify(paymentService, times(1)).getAllWithStatusInProgress();
        verify(paymentService, times(1)).updateTransactionStatus(eq(transaction.getId()), any(TransactionStatus.class));
        verify(accountService, never()).makeMoneyTransfer(any());
        verify(webhookService, never()).sendNotification(any());
    }
}
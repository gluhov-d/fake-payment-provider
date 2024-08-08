package com.github.gluhov.fakepaymentprovider.service;

import com.github.gluhov.fakepaymentprovider.model.Transaction;
import com.github.gluhov.fakepaymentprovider.model.TransactionStatus;
import com.github.gluhov.fakepaymentprovider.util.ProcessingUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.scheduler.VirtualTimeScheduler;

import static com.github.gluhov.fakepaymentprovider.service.TransactionData.transaction;
import static com.github.gluhov.fakepaymentprovider.service.TransactionData.updatedTransaction;
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
    public void testChangeTransactionStatus_Success() {
        VirtualTimeScheduler.getOrSet();

        Transaction successTransaction = Transaction.builder()
                .transactionStatus(TransactionStatus.SUCCESS)
                .build();

        when(paymentService.getAllWithStatusInProgress()).thenReturn(Flux.just(transaction));
        when(paymentService.updateTransactionStatus(any(), any())).thenReturn(Mono.just(successTransaction));
        when(accountService.makeMoneyTransfer(any())).thenReturn(Mono.empty());
        when(webhookService.sendNotification(any())).thenReturn(Mono.empty());


        processingService.changeTransactionStatus();

        verify(paymentService, times(1)).getAllWithStatusInProgress();
        verify(paymentService, times(1)).updateTransactionStatus(eq(transaction.getId()), any(TransactionStatus.class));
        verify(accountService, times(1)).makeMoneyTransfer(successTransaction);
        verify(webhookService, times(1)).sendNotification(successTransaction);
    }

    @Test
    public void testChangeTransactionStatus_Failure() {
        VirtualTimeScheduler.getOrSet();
        Transaction failureTransaction = Transaction.builder()
                .transactionStatus(TransactionStatus.FAILED)
                .build();
        when(paymentService.getAllWithStatusInProgress()).thenReturn(Flux.just(transaction));
        when(paymentService.updateTransactionStatus(any(), any())).thenReturn(Mono.just(failureTransaction));
        when(webhookService.sendNotification(any())).thenReturn(Mono.empty());

        processingService.changeTransactionStatus();

        verify(paymentService, times(1)).getAllWithStatusInProgress();
        verify(paymentService, times(1)).updateTransactionStatus(eq(transaction.getId()), any(TransactionStatus.class));
        verify(accountService, never()).makeMoneyTransfer(updatedTransaction);
        verify(webhookService, times(1)).sendNotification(failureTransaction);
    }

    @Test
    public void testChangeTransactionStatus_Error() {
        VirtualTimeScheduler.getOrSet();

        when(paymentService.getAllWithStatusInProgress()).thenReturn(Flux.just(transaction));
        when(paymentService.updateTransactionStatus(transaction.getId(), TransactionStatus.SUCCESS)).thenReturn(Mono.error(new RuntimeException("Update failed")));

        processingService.changeTransactionStatus();

        verify(paymentService, times(1)).getAllWithStatusInProgress();
        verify(paymentService, times(1)).updateTransactionStatus(eq(transaction.getId()), any(TransactionStatus.class));
        verify(accountService, never()).makeMoneyTransfer(any());
        verify(webhookService, never()).sendNotification(any());
    }
}
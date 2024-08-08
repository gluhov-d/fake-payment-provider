package com.github.gluhov.fakepaymentprovider.service;

import com.github.gluhov.fakepaymentprovider.exception.ProcessingException;
import com.github.gluhov.fakepaymentprovider.model.Transaction;
import com.github.gluhov.fakepaymentprovider.model.TransactionStatus;
import com.github.gluhov.fakepaymentprovider.model.Webhook;
import com.github.gluhov.fakepaymentprovider.repository.WebhookRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static com.github.gluhov.fakepaymentprovider.service.CardTestData.cardCustomerData;
import static com.github.gluhov.fakepaymentprovider.service.CustomerData.customerData;
import static com.github.gluhov.fakepaymentprovider.service.PaymentMethodData.paymentMethod;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class WebhookServiceTest {
    @Mock
    private WebhookRepository webhookRepository;

    @Mock
    private CardService cardService;

    @Mock
    private CustomerService customerService;

    @Mock
    private PaymentMethodService paymentMethodService;

    @InjectMocks
    private WebhookService webhookService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendNotification() {
        Webhook webhook = Webhook.builder()
                .transactionId(UUID.randomUUID())
                .message("OK")
                .transactionStatus(TransactionStatus.IN_PROGRESS)
                .build();

        Transaction transaction = new Transaction();
        transaction.setId(webhook.getTransactionId());
        transaction.setModifiedBy("system");
        transaction.setNotificationUrl("http://example.com");
        transaction.setTransactionStatus(TransactionStatus.IN_PROGRESS);

        when(webhookRepository.save(any(Webhook.class))).thenReturn(Mono.just(webhook));
        when(cardService.getById(any())).thenReturn(Mono.just(cardCustomerData));
        when(customerService.getById(any())).thenReturn(Mono.just(customerData));
        when(paymentMethodService.getById(any())).thenReturn(Mono.just(paymentMethod));

        Mono<Void> result = webhookService.sendNotification(transaction);

        StepVerifier.create(result)
                .expectComplete()
                .verify();
    }

    @Test
    public void testSendNotificationWithNullUrl() {
        Transaction transaction = new Transaction();
        transaction.setNotificationUrl(null);

        webhookService.sendNotification(transaction)
                .subscribe(entity -> Assertions.fail("Notification can not be sent"),
                        error -> {
                            assertTrue(error instanceof ProcessingException);
                            assertEquals("Notification can not be sent", error.getMessage());
                            assertEquals("FPP_PROCESSING_NOTIFICATION_ERROR", ((ProcessingException) error).getErrorCode());
                        });
    }

    @Test
    public void testSendNotificationWithEmptyUrl() {
        Transaction transaction = new Transaction();
        transaction.setNotificationUrl("");

        webhookService.sendNotification(transaction)
                .subscribe(entity -> Assertions.fail("Notification cannot be sent"),
                        error -> {
                            assertTrue(error instanceof ProcessingException);
                            assertEquals("Notification cannot be sent", error.getMessage());
                            assertEquals("FPP_PROCESSING_NOTIFICATION_ERROR", ((ProcessingException) error).getErrorCode());
                        });
    }
}
package com.github.gluhov.fakepaymentprovider.service;

import com.github.gluhov.fakepaymentprovider.exception.ProcessingException;
import com.github.gluhov.fakepaymentprovider.mapper.CardDataMapper;
import com.github.gluhov.fakepaymentprovider.mapper.CustomerMapper;
import com.github.gluhov.fakepaymentprovider.model.Transaction;
import com.github.gluhov.fakepaymentprovider.repository.WebhookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.github.gluhov.fakepaymentprovider.service.CardTestData.cardCustomerData;
import static com.github.gluhov.fakepaymentprovider.service.CardTestData.cardCustomerDataDto;
import static com.github.gluhov.fakepaymentprovider.service.CustomerData.customerData;
import static com.github.gluhov.fakepaymentprovider.service.CustomerData.customerDataDto;
import static com.github.gluhov.fakepaymentprovider.service.PaymentMethodData.paymentMethod;
import static com.github.gluhov.fakepaymentprovider.service.TransactionData.transaction;
import static com.github.gluhov.fakepaymentprovider.service.WebhookData.webhook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class WebhookServiceTest {

    @Mock
    private WebhookRepository webhookRepository;

    @Mock
    private CardService cardService;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private CustomerService customerService;

    @Mock
    private PaymentMethodService paymentMethodService;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private CardDataMapper cardDataMapper;

    @InjectMocks
    private WebhookService webhookService;

    @BeforeEach
    public void setUp() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestHeadersSpec.header(any(),any())).thenReturn(requestHeadersSpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());
    }

    @Test
    @DisplayName("Test send notification functionality")
    public void testSendNotification() {
        when(customerMapper.map(customerData)).thenReturn(customerDataDto);
        when(cardDataMapper.map(cardCustomerData)).thenReturn(cardCustomerDataDto);

        when(webhookRepository.save(any())).thenReturn(Mono.just(webhook));
        when(cardService.getById(any())).thenReturn(Mono.just(cardCustomerData));
        when(customerService.getById(any())).thenReturn(Mono.just(customerData));
        when(paymentMethodService.getById(any())).thenReturn(Mono.just(paymentMethod));

        Mono<Void> result = webhookService.sendNotification(transaction);

        StepVerifier.create(result)
                .expectComplete()
                .verify();

        verify(webhookRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Test send notification with null url functionality")
    public void testSendNotificationWithNullUrl() {
        Transaction transaction = new Transaction();
        transaction.setNotificationUrl(null);

        Mono<Void> result = webhookService.sendNotification(transaction);
        StepVerifier.create(result)
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof ProcessingException);
                    assertEquals("Notification can not be send", error.getMessage());
                    assertEquals("FPP_PROCESSING_NOTIFICATION_ERROR", ((ProcessingException) error).getErrorCode());
                })
                .verify();
    }

    @Test
    @DisplayName("Test send notification with empty url functionality")
    public void testSendNotificationWithEmptyUrl() {
        Transaction transaction = new Transaction();
        transaction.setNotificationUrl("");

        Mono<Void> result = webhookService.sendNotification(transaction);
        StepVerifier.create(result)
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof ProcessingException);
                    assertEquals("Notification can not be send", error.getMessage());
                    assertEquals("FPP_PROCESSING_NOTIFICATION_ERROR", ((ProcessingException) error).getErrorCode());
                })
                .verify();
    }
}
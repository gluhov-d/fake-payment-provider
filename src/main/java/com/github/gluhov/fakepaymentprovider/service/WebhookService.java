package com.github.gluhov.fakepaymentprovider.service;

import com.github.gluhov.fakepaymentprovider.dto.WebHookDto;
import com.github.gluhov.fakepaymentprovider.exception.ProcessingException;
import com.github.gluhov.fakepaymentprovider.mapper.CardDataMapper;
import com.github.gluhov.fakepaymentprovider.mapper.CustomerMapper;
import com.github.gluhov.fakepaymentprovider.model.Status;
import com.github.gluhov.fakepaymentprovider.model.Transaction;
import com.github.gluhov.fakepaymentprovider.model.Webhook;
import com.github.gluhov.fakepaymentprovider.repository.WebhookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebhookService {
    private final WebClient webClient;
    private final WebhookRepository webhookRepository;
    private final CardService cardService;
    private final CustomerService customerService;
    private final CardDataMapper cardDataMapper;
    private final CustomerMapper customerMapper;
    private final PaymentMethodService paymentMethodService;

    public Mono<Void> sendNotification(Transaction transaction) {
        log.info("Send notification with status: " + transaction.getTransactionStatus());
        if (transaction.getNotificationUrl() == null && transaction.getNotificationUrl().isEmpty()) {
            return Mono.error(new ProcessingException("Notification can not be send", "FPP_PROCESSING_NOTIFICATION_ERROR"));
        }
        LocalDateTime now = LocalDateTime.now();
        return webhookRepository.save(
                Webhook.builder()
                        .createdBy(transaction.getModifiedBy())
                        .modifiedBy(transaction.getModifiedBy())
                        .createdAt(now)
                        .updatedAt(now)
                        .message("OK")
                        .transactionId(transaction.getId())
                        .status(Status.ACTIVE)
                        .transactionStatus(transaction.getTransactionStatus())
                        .build())
                .flatMap(savedWebhook -> cardService.getById(transaction.getCardId())
                        .flatMap(existingCard -> customerService.getById(transaction.getCustomerId())
                                .flatMap(existingCustomer -> paymentMethodService.getById(transaction.getPaymentMethodId())
                                        .flatMap(existingPaymentMethod -> {
                                            return webClient.post()
                                                    .uri(transaction.getNotificationUrl())
                                                    .bodyValue(WebHookDto.builder()
                                                            .type(transaction.getType())
                                                            .amount(transaction.getAmount())
                                                            .language(transaction.getLanguage())
                                                            .paymentMethod(existingPaymentMethod.getType())
                                                            .cardData(cardDataMapper.map(existingCard))
                                                            .customerDto(customerMapper.map(existingCustomer))
                                                            .status(String.valueOf(transaction.getTransactionStatus()))
                                                            .message("OK")
                                                            .transactionId(transaction.getId())
                                                            .currency(transaction.getCurrency())
                                                            .build())
                                                    .retrieve()
                                                    .bodyToMono(Void.class)
                                                    .onErrorResume(e -> {
                                                        log.error("Failed to post webhook " + e.getMessage());
                                                        return Mono.empty();
                                                    });
                                        }))));
    }
}
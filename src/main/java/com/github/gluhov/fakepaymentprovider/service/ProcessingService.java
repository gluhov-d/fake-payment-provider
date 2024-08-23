package com.github.gluhov.fakepaymentprovider.service;

import com.github.gluhov.fakepaymentprovider.exception.ProcessingException;
import com.github.gluhov.fakepaymentprovider.model.TransactionStatus;
import com.github.gluhov.fakepaymentprovider.util.ProcessingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProcessingService {
    private final PaymentService paymentService;
    private final WebhookService webhookService;
    private final AccountService accountService;

    @Scheduled(fixedRate = 30000, initialDelay = 10000)
    @Transactional
    public Mono<Void> changeTransactionStatus() {
        return paymentService.getAllWithStatusInProgress()
                .flatMap(transaction ->
                        paymentService.updateTransactionStatus(transaction.getId(), ProcessingUtil.getRandomStatus())
                                .flatMap(updatedTransaction -> {
                                    if (updatedTransaction.getTransactionStatus().equals(TransactionStatus.SUCCESS)) {
                                        return accountService.makeMoneyTransfer(updatedTransaction)
                                                .then(webhookService.sendNotification(updatedTransaction)
                                                        .thenReturn(updatedTransaction));
                                    } else {
                                        return webhookService.sendNotification(updatedTransaction)
                                                .thenReturn(updatedTransaction);
                                    }
                                })
                                .onErrorResume(e -> Mono.error(new ProcessingException("Transaction not updated", "FPP_PROCESSING_ERROR")))
                )
                .then()
                .doOnError(e -> {
                    log.error("Transaction failed:" + e.getMessage());
                })
                .subscribeOn(Schedulers.boundedElastic());
    }
}
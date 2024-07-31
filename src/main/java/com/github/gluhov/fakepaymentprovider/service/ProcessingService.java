package com.github.gluhov.fakepaymentprovider.service;

import com.github.gluhov.fakepaymentprovider.exception.ProcessingException;
import com.github.gluhov.fakepaymentprovider.model.TransactionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProcessingService {
    private final PaymentService paymentService;
    private final WebhookService webhookService;
    private final AccountService accountService;
    private static final TransactionStatus[] STATUSES = {TransactionStatus.FAILED, TransactionStatus.SUCCESS};


    private final TransactionalOperator transactionalOperator;

    @Scheduled(fixedRate = 30000, initialDelay = 10000)
    public void changeTransactionStatus() {
        transactionalOperator.execute(transactionStatus ->
                        paymentService.getAllWithStatusInProgress()
                                .flatMap(transaction ->
                                        paymentService.updateTransactionStatus(transaction.getId(), getRandomStatus())
                                                .flatMap(updatedTransaction -> {
                                                    if (updatedTransaction.getTransactionStatus().equals(TransactionStatus.SUCCESS)) {
                                                        return accountService.makeMoneyTransfer(updatedTransaction)
                                                                .flatMap(trans -> webhookService.sendNotification(updatedTransaction)
                                                                        .thenReturn(updatedTransaction));
                                                    } else {
                                                        return webhookService.sendNotification(updatedTransaction)
                                                                .thenReturn(updatedTransaction);
                                                    }
                                                })
                                                .onErrorResume(e -> Mono.error(new ProcessingException("Transaction not updated", "FPP_PROCESSING_ERROR")))
                                )
                                .then()
                                .doOnError(e -> transactionStatus.setRollbackOnly())
                )
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }



    private TransactionStatus getRandomStatus() {
        int randomIndex = (int) (Math.random() * STATUSES.length);
        TransactionStatus randomStatus = STATUSES[randomIndex];
        log.debug("Generated random status: {}", randomStatus);
        return randomStatus;
    }
}
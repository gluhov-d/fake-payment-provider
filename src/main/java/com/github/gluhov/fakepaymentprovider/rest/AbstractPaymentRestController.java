package com.github.gluhov.fakepaymentprovider.rest;

import com.github.gluhov.fakepaymentprovider.dto.TransactionDto;
import com.github.gluhov.fakepaymentprovider.dto.TransactionDtoListResponse;
import com.github.gluhov.fakepaymentprovider.service.PaymentService;
import com.github.gluhov.fakepaymentprovider.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.UUID;

public abstract class AbstractPaymentRestController {
    @Autowired
    protected PaymentService paymentService;

    public Mono<?> getByIdAndType(UUID id, String type) {
        return paymentService.getByIdAndType(id, type).map(transaction -> ResponseEntity.ok().body(transaction));
    }

    public Mono<?> getBetweenByType(Long startDate, Long endDate, UUID uuid, String type) {
        return paymentService.getBetweenByType(DateTimeUtil.dayOrMin(startDate), DateTimeUtil.dayOrMax(endDate), uuid, type)
                .collectList()
                .flatMap(transactionDtos -> {
                    TransactionDtoListResponse response = new TransactionDtoListResponse(transactionDtos);
                    return Mono.just(ResponseEntity.ok().body(response));
                });
    }

    public Mono<?> createTransaction(TransactionDto transactionDto, UUID uuid, String type) {
        return paymentService.createTransaction(transactionDto, uuid, type).map(transactionResponseDto -> ResponseEntity.ok().body(transactionResponseDto));
    }
}
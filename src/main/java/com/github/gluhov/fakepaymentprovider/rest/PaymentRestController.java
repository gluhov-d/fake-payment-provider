package com.github.gluhov.fakepaymentprovider.rest;

import com.github.gluhov.fakepaymentprovider.dto.TransactionDto;
import com.github.gluhov.fakepaymentprovider.dto.TransactionDtoListResponse;
import com.github.gluhov.fakepaymentprovider.security.CustomPrincipal;
import com.github.gluhov.fakepaymentprovider.service.PaymentService;
import com.github.gluhov.fakepaymentprovider.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PaymentRestController {
    public static final String REST_URL = "/api/v1/payments/";
    private final PaymentService paymentService;

    @GetMapping(value = REST_URL + "transaction/{id}/details")
    public Mono<?> getById(@PathVariable UUID id) {
        return paymentService.getById(id).map(transaction -> ResponseEntity.ok().body(transaction));
    }

    @GetMapping(value = REST_URL + "transaction/list")
    public Mono<?> getBetween(@RequestParam("start_date") @Nullable Long startDate,
                              @RequestParam("end_date") @Nullable Long endDate,
                              Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return paymentService.getBetween(DateTimeUtil.dayOrMin(startDate), DateTimeUtil.dayOrMax(endDate), customPrincipal.getUuid())
                .collectList()
                .flatMap(transactionDtos -> {
                    TransactionDtoListResponse response = new TransactionDtoListResponse(transactionDtos);
                    return Mono.just(ResponseEntity.ok().body(response));
                });
    }

    @PostMapping(value = REST_URL + "transaction")
    public Mono<?> topUp(@RequestBody TransactionDto transactionDto, Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return paymentService.topUp(transactionDto, customPrincipal.getUuid());
    }
}
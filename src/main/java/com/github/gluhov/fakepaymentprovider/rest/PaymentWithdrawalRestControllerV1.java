package com.github.gluhov.fakepaymentprovider.rest;

import com.github.gluhov.fakepaymentprovider.dto.TransactionDto;
import com.github.gluhov.fakepaymentprovider.security.CustomPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PaymentWithdrawalRestControllerV1 extends AbstractPaymentRestController{
    public static final String REST_URL = "/api/v1/payments/payout";
    public static final String TYPE = "payout";

    @GetMapping(value = REST_URL + "/{id}/details")
    public Mono<?> getById(@PathVariable UUID id) {
        return super.getByIdAndType(id, TYPE);
    }

    @GetMapping(value = REST_URL + "/list")
    public Mono<?> getBetween(@RequestParam("start_date") @Nullable Long startDate,
                              @RequestParam("end_date") @Nullable Long endDate,
                              Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return super.getBetweenByType(startDate, endDate, customPrincipal.getUuid(), TYPE);
    }

    @PostMapping(value = REST_URL)
    public Mono<?> payout(@RequestBody TransactionDto transactionDto, Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return super.createTransaction(transactionDto, customPrincipal.getUuid(), TYPE);
    }
}
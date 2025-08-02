package com.github.gluhov.fakepaymentprovider.rest;

import com.github.gluhov.fakepaymentprovider.dto.TransactionDto;
import com.github.gluhov.fakepaymentprovider.security.CustomPrincipal;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@OpenAPIDefinition(info = @Info(title = "PaymentTopUpRestController", version = "1.0", description = "WebFlux"))
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PaymentTopUpRestControllerV1 extends AbstractPaymentRestController{
    public static final String REST_URL = "/api/v1/payments/transaction";
    public static final String TYPE = "transaction";

    @GetMapping(value = REST_URL + "/{id}/details")
    @Operation(summary = "Get transaction", description = "Return transaction by id and type")
    public Mono<?> getById(@PathVariable UUID id) {
        return super.getByIdAndType(id, TYPE);
    }

    @GetMapping(value = REST_URL + "/list")
    @Operation(summary = "Get transaction list", description = "Return transaction list filtered by date range")
    public Mono<?> getBetween(@RequestParam("start_date") @Nullable Long startDate,
                              @RequestParam("end_date") @Nullable Long endDate,
                              Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return super.getBetweenByType(startDate, endDate, customPrincipal.getUuid(), TYPE);
    }

    @PostMapping(value = REST_URL)
    @Operation(summary = "Create transaction", description = "Create new transaction for top up")
    public Mono<?> topUp(@RequestBody TransactionDto transactionDto, Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return super.createTransaction(transactionDto, customPrincipal.getUuid(), TYPE);
    }
}
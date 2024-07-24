package com.github.gluhov.fakepaymentprovider.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.gluhov.fakepaymentprovider.model.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TransactionDto {

    private UUID transactionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String notificationUrl;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String type;
    private String currency;
    private Long amount;
    private String language;
    private String message;
    private TransactionStatus transactionStatus;
    private CustomerDto customer;
    private CardDataDto cardData;
    private PaymentMethodDto paymentMethod;
}
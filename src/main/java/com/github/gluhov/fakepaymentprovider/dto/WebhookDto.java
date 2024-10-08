package com.github.gluhov.fakepaymentprovider.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class WebhookDto {
    private UUID transactionId;
    private String paymentMethod;
    private Long amount;
    private String currency;
    private String type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CardDataDto cardData;
    private String language;
    private CustomerDto customerDto;
    private String status;
    private String message;
}
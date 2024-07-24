package com.github.gluhov.fakepaymentprovider.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.gluhov.fakepaymentprovider.util.JsonExpirationDateDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CardDataDto {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String cardNumber;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonDeserialize(using = JsonExpirationDateDeserializer.class)
    private LocalDate expDate;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int cvv;

    @JsonProperty("card_number")
    public String getMaskedCardNumber() {
        if (cardNumber == null || cardNumber.length() < 8) {
            log.error("Card number length is less than 8 digits");
            return cardNumber;
        }
        return cardNumber.substring(0, 4) + "****" + cardNumber.substring(cardNumber.length() - 4);
    }
}
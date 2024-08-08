package com.github.gluhov.fakepaymentprovider.service;

import com.github.gluhov.fakepaymentprovider.dto.CardDataDto;
import com.github.gluhov.fakepaymentprovider.model.CardData;
import com.github.gluhov.fakepaymentprovider.model.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.github.gluhov.fakepaymentprovider.service.AccountData.ACCOUNT_CUSTOMER_UUID;

public class CardTestData {
    public static final UUID CARD_UUID = UUID.fromString("1b09f680-1c67-11ec-9621-0242ac130002");
    public static final UUID CARD_NOT_FOUND_UUID = UUID.fromString("1b09f680-1c67-11ec-9621-0242ac130500");
    public static final CardData cardCustomerData = new CardData(CARD_UUID, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), "system", "system", "1234567812345678", LocalDate.of(2025, 12,31), 123, ACCOUNT_CUSTOMER_UUID);
    public static final CardDataDto cardCustomerDataDto = new CardDataDto(cardCustomerData);
}
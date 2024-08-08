package com.github.gluhov.fakepaymentprovider.service;

import com.github.gluhov.fakepaymentprovider.model.Merchant;
import com.github.gluhov.fakepaymentprovider.model.Status;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.github.gluhov.fakepaymentprovider.service.AccountData.ACCOUNT_MERCHANT_UUID;

public class MerchantData {
    public static final UUID MERCHANT_UUID = UUID.fromString("4c09f680-1c67-11ec-9621-0242ac130002");
    public static final UUID MERCHANT_NOT_FOUND_UUID = UUID.fromString("4c09f680-1c67-11ec-9621-0242ac130500");
    public static final Merchant merchant = new Merchant(MERCHANT_UUID, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), "system", "system", ACCOUNT_MERCHANT_UUID,  "secret123", "PROSELYTE");
}
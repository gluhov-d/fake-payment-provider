package com.github.gluhov.fakepaymentprovider.service;

import com.github.gluhov.fakepaymentprovider.model.Account;
import com.github.gluhov.fakepaymentprovider.model.Status;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.github.gluhov.fakepaymentprovider.service.CustomerData.CUSTOMER_UUID;
import static com.github.gluhov.fakepaymentprovider.service.MerchantData.MERCHANT_UUID;

public class AccountData {
    public static final UUID ACCOUNT_CUSTOMER_UUID = UUID.fromString("2a09f680-1c67-11ec-9621-0242ac130002");
    public static final UUID ACCOUNT_MERCHANT_UUID = UUID.fromString("2a09f680-1c67-11ec-9621-0242ac130004");
    public static final UUID ACCOUNT_NOT_FOUND_UUID = UUID.fromString("2a09f680-1c67-11ec-9621-0242ac130500");
    public static final Account accountCustomer = new Account(ACCOUNT_CUSTOMER_UUID, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), "system", "system", 100000L, "USD", CUSTOMER_UUID, "customer");
    public static final Account  accountMerchant = new Account(ACCOUNT_MERCHANT_UUID, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), "system", "system", 200000L, "EUR", MERCHANT_UUID, "merchant");
    public static final Account accountUpdatedCustomer = new Account(ACCOUNT_CUSTOMER_UUID, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), "system", "system", 100000L - 500L, "USD", CUSTOMER_UUID, "customer");
    public static final Account accountUpdatedMerchant = new Account(ACCOUNT_MERCHANT_UUID, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), "system", "system", 200000L + 500L, "EUR", MERCHANT_UUID, "merchant");
}
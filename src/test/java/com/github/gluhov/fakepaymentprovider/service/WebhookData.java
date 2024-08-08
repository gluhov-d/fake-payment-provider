package com.github.gluhov.fakepaymentprovider.service;

import com.github.gluhov.fakepaymentprovider.model.Status;
import com.github.gluhov.fakepaymentprovider.model.Webhook;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.github.gluhov.fakepaymentprovider.service.TransactionData.TRANSACTION_UUID;
import static com.github.gluhov.fakepaymentprovider.service.TransactionData.transaction;

public class WebhookData {
    public static final UUID WEBHOOK_UUID =UUID.fromString("7f09f680-1c67-11ec-9621-0242ac130002");
    public static final Webhook webhook = new Webhook(WEBHOOK_UUID, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), "system", "system", TRANSACTION_UUID, transaction.getMessage(), transaction.getTransactionStatus());
}
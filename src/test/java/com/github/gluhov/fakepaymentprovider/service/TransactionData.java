package com.github.gluhov.fakepaymentprovider.service;

import com.github.gluhov.fakepaymentprovider.dto.TransactionDto;
import com.github.gluhov.fakepaymentprovider.model.Status;
import com.github.gluhov.fakepaymentprovider.model.Transaction;
import com.github.gluhov.fakepaymentprovider.model.TransactionStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.github.gluhov.fakepaymentprovider.service.CardTestData.CARD_UUID;
import static com.github.gluhov.fakepaymentprovider.service.CardTestData.cardCustomerDataDto;
import static com.github.gluhov.fakepaymentprovider.service.CustomerData.*;
import static com.github.gluhov.fakepaymentprovider.service.MerchantData.MERCHANT_UUID;
import static com.github.gluhov.fakepaymentprovider.service.MerchantData.merchant;
import static com.github.gluhov.fakepaymentprovider.service.PaymentMethodData.*;

public class TransactionData {
    public static final UUID TRANSACTION_UUID = UUID.fromString("6e09f680-1c67-11ec-9621-0242ac130002");
    public static final UUID PAYOUT_UUID = UUID.fromString("6e09f680-1c67-11ec-9621-0242ac130003");
    public static final UUID TRANSACTION_NOT_FOUND_UUID = UUID.fromString("6e09f680-1c67-11ec-9621-0242ac130500");
    public static final LocalDateTime START_DATE = LocalDate.now().atStartOfDay();
    public static final LocalDateTime END_DATE = LocalDate.now().atTime(LocalTime.MAX);
    public static final Transaction transaction = new Transaction(TRANSACTION_UUID, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), "system", "system", "https://proselyte.net/webhook/transaction", "transaction",  "USD", 500L, "en", "OK", TransactionStatus.IN_PROGRESS, merchant, MERCHANT_UUID, customerData, CUSTOMER_UUID, CARD_UUID, paymentMethod, PAYMENT_METHOD_UUID);
    public static final Transaction updatedTransaction = new Transaction(TRANSACTION_UUID, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), "system", "system", "https://proselyte.net/webhook/transaction", "transaction",  "USD", 500L, "en", "OK", TransactionStatus.SUCCESS, merchant, MERCHANT_UUID, customerData, CUSTOMER_UUID, CARD_UUID, paymentMethod, PAYMENT_METHOD_UUID);
    public static final Transaction payout = new Transaction(PAYOUT_UUID, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), "system", "system", "https://proselyte.net/webhook/payout", "payout",  "EUR", 200L, "de", "OK", TransactionStatus.IN_PROGRESS, null, UUID.fromString("4c09f680-1c67-11ec-9621-0242ac130002"), null, UUID.fromString("1b09f680-1c67-11ec-9621-0242ac130003"), UUID.fromString("3e09f680-1c67-11ec-9621-0242ac130003"), null, UUID.fromString("5d09f680-1c67-11ec-9621-0242ac130002"));
    public static final List<Transaction> transactionsInProgress = Arrays.asList(transaction, payout);
    public static final TransactionDto transactionDto = TransactionDto.builder()
            .transactionId(transaction.getId())
            .createdAt(transaction.getCreatedAt())
            .updatedAt(transaction.getUpdatedAt())
            .transactionStatus(transaction.getTransactionStatus())
            .amount(transaction.getAmount())
            .notificationUrl(transaction.getNotificationUrl())
            .message(transaction.getMessage())
            .currency(transaction.getCurrency())
            .language(transaction.getLanguage())
            .type(transaction.getType())
            .cardData(cardCustomerDataDto)
            .customer(customerDataDto)
            .paymentMethod(paymentMethodDto)
            .build();

}
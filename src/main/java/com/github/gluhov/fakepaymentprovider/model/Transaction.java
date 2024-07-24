package com.github.gluhov.fakepaymentprovider.model;

import lombok.*;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("transaction")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Transaction extends BaseEntity {
    @Column("notification_url")
    private String notificationUrl;
    @Column("type")
    private String type;
    @Column("currency")
    private String currency;
    @Column("amount")
    private Long amount;
    @Column("language")
    private String language;
    @Column("message")
    private String message;
    @Column("transaction_status")
    private TransactionStatus transactionStatus;
    @Transient
    private Merchant merchant;
    @Column("merchant_id")
    private UUID merchantId;
    @Transient
    private Customer customer;
    @Column("customer_id")
    private UUID customerId;
    @Transient
    private PaymentMethod paymentMethod;
    @Column("payment_method_id")
    private UUID paymentMethodId;

    @Builder
    public Transaction(UUID id, Status status, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy,
                       String modifiedBy, String notificationUrl, String type, String currency, Long amount, String language,
                       String message, TransactionStatus transactionStatus, Merchant merchant, UUID merchantId, Customer customer,
                       UUID customerId, PaymentMethod paymentMethod, UUID paymentMethodId) {
        super(id, status, createdAt, updatedAt, createdBy, modifiedBy);
        this.notificationUrl = notificationUrl;
        this.type = type;
        this.currency = currency;
        this.amount = amount;
        this.language = language;
        this.message = message;
        this.transactionStatus = transactionStatus;
        this.merchant = merchant;
        this.merchantId = merchantId;
        this.customer = customer;
        this.customerId = customerId;
        this.paymentMethod = paymentMethod;
        this.paymentMethodId = paymentMethodId;
    }
}
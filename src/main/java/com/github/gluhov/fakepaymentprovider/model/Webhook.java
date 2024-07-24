package com.github.gluhov.fakepaymentprovider.model;

import lombok.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("webhook")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Webhook extends BaseEntity {

    private Transaction transaction;
    @Column("message")
    private String message;
    private TransactionStatus transactionStatus;

    @Builder

    public Webhook(UUID id, Status status, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String modifiedBy, Transaction transaction, String message, TransactionStatus transactionStatus) {
        super(id, status, createdAt, updatedAt, createdBy, modifiedBy);
        this.transaction = transaction;
        this.message = message;
        this.transactionStatus = transactionStatus;
    }
}
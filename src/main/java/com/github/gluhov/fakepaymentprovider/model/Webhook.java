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

    @Column("transaction_id")
    private UUID transactionId;
    @Column("message")
    private String message;
    @Column("transaction_status")
    private TransactionStatus transactionStatus;

    @Builder
    public Webhook(UUID id, Status status, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String modifiedBy, UUID transactionId, String message, TransactionStatus transactionStatus) {
        super(id, status, createdAt, updatedAt, createdBy, modifiedBy);
        this.transactionId = transactionId;
        this.message = message;
        this.transactionStatus = transactionStatus;
    }
}
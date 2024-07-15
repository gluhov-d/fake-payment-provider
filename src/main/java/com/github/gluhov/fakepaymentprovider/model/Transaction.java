package com.github.gluhov.fakepaymentprovider.model;

import lombok.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("transaction")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Transaction extends BaseEntity {
    @Column("notification_url")
    private String notificationUrl;
    @Column("type")
    private String type;
    @Column("currency")
    private String currency;
    @Column("amount")
    private long amount;
    @Column("language")
    private String language;
    @Column("message")
    private String message;
    private TransactionStatus transactionStatus;
}

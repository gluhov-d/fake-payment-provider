package com.github.gluhov.fakepaymentprovider.model;

import lombok.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("webhook")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Webhook extends BaseEntity {

    private Transaction transaction;
    @Column("message")
    private String message;
    private TransactionStatus transactionStatus;

}

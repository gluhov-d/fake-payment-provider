package com.github.gluhov.fakepaymentprovider.model;

import lombok.*;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("account")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Account extends BaseEntity {
    @Column("balance")
    private Long balance;
    @Column("currency")
    private String currency;
    @Column("card_id")
    private UUID cardId;
    @Transient
    private CardData cardData;

    @Builder
    public Account(UUID id, Status status, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String modifiedBy, Long balance, String currency, UUID cardId, CardData cardData) {
        super(id, status, createdAt, updatedAt, createdBy, modifiedBy);
        this.balance = balance;
        this.currency = currency;
        this.cardId = cardId;
        this.cardData = cardData;
    }
}
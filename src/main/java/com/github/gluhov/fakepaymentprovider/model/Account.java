package com.github.gluhov.fakepaymentprovider.model;

import lombok.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("account")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Account extends BaseEntity {
    @Column("balance")
    private long balance;
    @Column("currency")
    private String currency;
    private CardData cardData;
}
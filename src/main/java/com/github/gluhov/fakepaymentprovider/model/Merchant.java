package com.github.gluhov.fakepaymentprovider.model;

import lombok.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("merchant")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Merchant extends BaseEntity {
    private Account account;
    @Column("secret_key")
    private String secretKey;
}

package com.github.gluhov.fakepaymentprovider.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("merchant")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Merchant extends BaseEntity {
    @Column("account_id")
    private UUID accountId;
    @Transient
    private Account account;
    @Column("secret_key")
    @NotBlank
    @Size(min = 2)
    private String secretKey;
    @Column("merchant_id")
    @NotBlank
    @Size(min = 2)
    private String merchantId;
}
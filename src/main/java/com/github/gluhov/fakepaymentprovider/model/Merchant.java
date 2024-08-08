package com.github.gluhov.fakepaymentprovider.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("merchant")
@Data
@AllArgsConstructor
@NoArgsConstructor
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

    @Builder
    public Merchant(UUID id, Status status, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String modifiedBy, UUID accountId, String secretKey, String merchantId) {
        super(id, status, createdAt, updatedAt, createdBy, modifiedBy);
        this.accountId = accountId;
        this.secretKey = secretKey;
        this.merchantId = merchantId;
    }
}
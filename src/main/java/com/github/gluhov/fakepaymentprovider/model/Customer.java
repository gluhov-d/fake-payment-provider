package com.github.gluhov.fakepaymentprovider.model;

import lombok.*;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("customer")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Customer extends BaseEntity {

    @Column("first_name")
    private String firstName;
    @Column("last_name")
    private String lastName;
    @Column("country")
    private String country;
    @Column("account_id")
    private UUID accountId;
    @Transient
    private Account account;

    @Builder
    public Customer(UUID id, Status status, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String modifiedBy, String firstName, String lastName, String country, UUID accountId) {
        super(id, status, createdAt, updatedAt, createdBy, modifiedBy);
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.accountId = accountId;
    }
}
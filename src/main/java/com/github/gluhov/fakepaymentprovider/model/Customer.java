package com.github.gluhov.fakepaymentprovider.model;

import lombok.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("customer")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Customer extends BaseEntity {

    @Column("first_name")
    private String firstName;
    @Column("last_name")
    private String lastName;
    @Column("country")
    private String country;
    private Account account;
}

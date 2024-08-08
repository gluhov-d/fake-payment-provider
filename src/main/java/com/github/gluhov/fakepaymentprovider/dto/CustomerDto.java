package com.github.gluhov.fakepaymentprovider.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.gluhov.fakepaymentprovider.model.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CustomerDto {
    private String firstName;
    private String lastName;
    private String country;

    public CustomerDto(Customer customer) {
        this.firstName = customer.getFirstName();
        this.lastName = customer.getLastName();
        this.country = customer.getCountry();
    }
}
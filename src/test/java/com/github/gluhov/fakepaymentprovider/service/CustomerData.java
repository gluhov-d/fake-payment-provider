package com.github.gluhov.fakepaymentprovider.service;

import com.github.gluhov.fakepaymentprovider.dto.CustomerDto;
import com.github.gluhov.fakepaymentprovider.model.Customer;
import com.github.gluhov.fakepaymentprovider.model.Status;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.github.gluhov.fakepaymentprovider.service.AccountData.ACCOUNT_CUSTOMER_UUID;

public class CustomerData {
    public static final UUID CUSTOMER_UUID = UUID.fromString("1b09f680-1c67-11ec-9621-0242ac130002");
    public static final UUID CUSTOMER_NOT_FOUND_UUID = UUID.fromString("1b09f680-1c67-11ec-9621-0242ac130500");
    public static final Customer customerData = new Customer(CUSTOMER_UUID, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), "system", "system", "John", "Doel", "BRL", ACCOUNT_CUSTOMER_UUID);
    public static final CustomerDto customerDataDto = new CustomerDto(customerData);
}
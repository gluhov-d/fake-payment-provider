package com.github.gluhov.fakepaymentprovider.mapper;

import com.github.gluhov.fakepaymentprovider.dto.CustomerDto;
import com.github.gluhov.fakepaymentprovider.model.Customer;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerDto map(Customer customer);

    @InheritInverseConfiguration
    Customer map(CustomerDto customerDto);
}
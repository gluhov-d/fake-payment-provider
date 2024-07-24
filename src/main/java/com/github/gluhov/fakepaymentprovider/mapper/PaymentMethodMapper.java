package com.github.gluhov.fakepaymentprovider.mapper;

import com.github.gluhov.fakepaymentprovider.dto.PaymentMethodDto;
import com.github.gluhov.fakepaymentprovider.model.PaymentMethod;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMethodMapper {
    PaymentMethodDto map(PaymentMethod paymentMethod);

    @InheritInverseConfiguration
    PaymentMethod map(PaymentMethodDto paymentMethodDto);
}
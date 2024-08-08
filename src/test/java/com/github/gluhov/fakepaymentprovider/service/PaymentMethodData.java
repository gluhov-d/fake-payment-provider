package com.github.gluhov.fakepaymentprovider.service;

import com.github.gluhov.fakepaymentprovider.dto.PaymentMethodDto;
import com.github.gluhov.fakepaymentprovider.model.PaymentMethod;
import com.github.gluhov.fakepaymentprovider.model.Status;

import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentMethodData {
    public static final UUID PAYMENT_METHOD_UUID = UUID.fromString("5d09f680-1c67-11ec-9621-0242ac130002");
    public static final UUID PAYMENT_METHOD_NOT_FOUND_UUID = UUID.fromString("5d09f680-1c67-11ec-9621-0242ac130500");
    public static final PaymentMethod paymentMethod = new PaymentMethod(PAYMENT_METHOD_UUID, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), "system", "system", "CARD");
    public static final PaymentMethodDto paymentMethodDto = new PaymentMethodDto(paymentMethod);
}
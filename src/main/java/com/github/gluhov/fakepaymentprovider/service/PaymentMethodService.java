package com.github.gluhov.fakepaymentprovider.service;

import com.github.gluhov.fakepaymentprovider.exception.EntityNotFoundException;
import com.github.gluhov.fakepaymentprovider.model.PaymentMethod;
import com.github.gluhov.fakepaymentprovider.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentMethodService {
    private final PaymentMethodRepository paymentMethodRepository;

    public Mono<PaymentMethod> getById(UUID uuid) {
        return paymentMethodRepository.findById(uuid)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Payment method not found", "FPP_PAYMENT_METHOD_NOT_ALLOWED")));
    }

    public Mono<PaymentMethod> findByType(String type) {
        return paymentMethodRepository.findPaymentMethodByType(type)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Payment method not found", "FPP_PAYMENT_METHOD_NOT_ALLOWED")));
    }
}
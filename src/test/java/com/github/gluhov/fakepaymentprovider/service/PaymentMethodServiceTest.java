package com.github.gluhov.fakepaymentprovider.service;

import com.github.gluhov.fakepaymentprovider.exception.EntityNotFoundException;
import com.github.gluhov.fakepaymentprovider.model.PaymentMethod;
import com.github.gluhov.fakepaymentprovider.repository.PaymentMethodRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.github.gluhov.fakepaymentprovider.service.PaymentMethodData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class PaymentMethodServiceTest {
    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @InjectMocks
    private PaymentMethodService paymentMethodService;

    @Test
    @DisplayName("Test get by id functionality")
    void getById() {
        when(paymentMethodRepository.findById(PAYMENT_METHOD_UUID)).thenReturn(Mono.just(paymentMethod));

        Mono<PaymentMethod> result = paymentMethodService.getById(PAYMENT_METHOD_UUID);
        StepVerifier.create(result)
                        .expectNext(paymentMethod)
                                .verifyComplete();
        verify(paymentMethodRepository, times(1)).findById(PAYMENT_METHOD_UUID);
    }

    @Test
    @DisplayName("Test get by not found id functionality")
    void getNotFound() {
        when(paymentMethodRepository.findById(PAYMENT_METHOD_NOT_FOUND_UUID)).thenReturn(Mono.empty());

        Mono<PaymentMethod> result = paymentMethodService.getById(PAYMENT_METHOD_NOT_FOUND_UUID);
        StepVerifier.create(result)
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof EntityNotFoundException);
                    assertEquals("Payment method not found", error.getMessage());
                    assertEquals("FPP_PAYMENT_METHOD_NOT_ALLOWED", ((EntityNotFoundException) error).getErrorCode());
                }).verify();
    }

    @Test
    @DisplayName("Test find by payment method type functionality")
    void findByType() {
        when(paymentMethodRepository.findPaymentMethodByType(paymentMethod.getType())).thenReturn(Mono.just(paymentMethod));

        Mono<PaymentMethod> result = paymentMethodService.findByType(paymentMethod.getType());
        StepVerifier.create(result)
                        .expectNext(paymentMethod)
                                .verifyComplete();
        verify(paymentMethodRepository, times(1)).findPaymentMethodByType(paymentMethod.getType());
    }
}
package com.github.gluhov.fakepaymentprovider.service;

import com.github.gluhov.fakepaymentprovider.exception.EntityNotFoundException;
import com.github.gluhov.fakepaymentprovider.model.PaymentMethod;
import com.github.gluhov.fakepaymentprovider.repository.PaymentMethodRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import static com.github.gluhov.fakepaymentprovider.service.PaymentMethodData.*;
import static org.assertj.core.api.AssertionsForClassTypes.fail;
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
    void getById() {
        when(paymentMethodRepository.findById(PAYMENT_METHOD_UUID)).thenReturn(Mono.just(paymentMethod));

        Mono<PaymentMethod> result = paymentMethodService.getById(PAYMENT_METHOD_UUID);
        assertEquals(paymentMethod, result.block());
        verify(paymentMethodRepository, times(1)).findById(PAYMENT_METHOD_UUID);
    }

    @Test
    void getNotFound() {
        when(paymentMethodRepository.findById(PAYMENT_METHOD_NOT_FOUND_UUID)).thenReturn(Mono.empty());

        paymentMethodService.getById(PAYMENT_METHOD_NOT_FOUND_UUID).subscribe(entity -> fail("Expected an EntityNotFoundException to be thrown"),
                error -> {
                    assertTrue(error instanceof EntityNotFoundException);
                    assertEquals("Account not found", error.getMessage());
                    assertEquals("FPP_ACCOUNT_NOT_FOUND", ((EntityNotFoundException) error).getErrorCode());
                });
    }

    @Test
    void findByType() {
        when(paymentMethodRepository.findPaymentMethodByType(paymentMethod.getType())).thenReturn(Mono.just(paymentMethod));

        Mono<PaymentMethod> result = paymentMethodService.findByType(paymentMethod.getType());
        assertEquals(paymentMethod, result.block());
        verify(paymentMethodRepository, times(1)).findPaymentMethodByType(paymentMethod.getType());
    }
}
package com.github.gluhov.fakepaymentprovider.service;

import com.github.gluhov.fakepaymentprovider.exception.EntityNotFoundException;
import com.github.gluhov.fakepaymentprovider.model.Merchant;
import com.github.gluhov.fakepaymentprovider.repository.MerchantRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.github.gluhov.fakepaymentprovider.service.MerchantData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class MerchantServiceTest {
    @Mock
    private MerchantRepository merchantRepository;

    @InjectMocks
    private MerchantService merchantService;

    @Test
    @DisplayName("Test get bi id functionality")
    void getById() {
        when(merchantRepository.findById(MERCHANT_UUID)).thenReturn(Mono.just(merchant));

        Mono<Merchant> result = merchantService.getById(MERCHANT_UUID);
        StepVerifier.create(result)
                        .expectNext(merchant)
                                .verifyComplete();
        verify(merchantRepository, times(1)).findById(MERCHANT_UUID);
    }

    @Test
    @DisplayName("Test find by merchant id and secret key functionality")
    void findByIdAndSecretKey() {
        when(merchantRepository.findByMerchantIdAndSecretKey(merchant.getMerchantId(), merchant.getSecretKey())).thenReturn(Mono.just(merchant));

        Mono<Merchant> result = merchantService.findByIdAndSecretKey(merchant.getMerchantId(), merchant.getSecretKey());
        StepVerifier.create(result)
                        .expectNext(merchant)
                                .verifyComplete();
        verify(merchantRepository, times(1)).findByMerchantIdAndSecretKey(merchant.getMerchantId(), merchant.getSecretKey());
    }

    @Test
    @DisplayName("Test get by not found id functionality")
    void getNotFound() {
        when(merchantRepository.findById(MERCHANT_NOT_FOUND_UUID)).thenReturn(Mono.empty());

        Mono<Merchant> result =merchantService.getById(MERCHANT_NOT_FOUND_UUID);
        StepVerifier.create(result)
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof EntityNotFoundException);
                    assertEquals("Merchant not found", error.getMessage());
                    assertEquals("FPP_MERCHANT_NOT_FOUND", ((EntityNotFoundException) error).getErrorCode());
                }).verify();
    }
}
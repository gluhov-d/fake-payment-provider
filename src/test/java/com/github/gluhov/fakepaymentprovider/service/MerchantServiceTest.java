package com.github.gluhov.fakepaymentprovider.service;

import com.github.gluhov.fakepaymentprovider.exception.EntityNotFoundException;
import com.github.gluhov.fakepaymentprovider.model.Merchant;
import com.github.gluhov.fakepaymentprovider.repository.MerchantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import static com.github.gluhov.fakepaymentprovider.service.MerchantData.*;
import static org.assertj.core.api.AssertionsForClassTypes.fail;
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
    void getById() {
        when(merchantRepository.findById(MERCHANT_UUID)).thenReturn(Mono.just(merchant));

        Mono<Merchant> result = merchantService.getById(MERCHANT_UUID);
        assertEquals(merchant, result.block());
        verify(merchantRepository, times(1)).findById(MERCHANT_UUID);
    }

    @Test
    void findByIdAndSecretKey() {
        when(merchantRepository.findByMerchantIdAndSecretKey(merchant.getMerchantId(), merchant.getSecretKey())).thenReturn(Mono.just(merchant));

        Mono<Merchant> result = merchantService.findByIdAndSecretKey(merchant.getMerchantId(), merchant.getSecretKey());
        assertEquals(merchant, result.block());
        verify(merchantRepository, times(1)).findByMerchantIdAndSecretKey(merchant.getMerchantId(), merchant.getSecretKey());
    }

    @Test
    void getNotFound() {
        when(merchantRepository.findById(MERCHANT_NOT_FOUND_UUID)).thenReturn(Mono.empty());

        merchantService.getById(MERCHANT_NOT_FOUND_UUID).subscribe(entity -> fail("Expected an EntityNotFoundException to be thrown"),
                error -> {
                    assertTrue(error instanceof EntityNotFoundException);
                    assertEquals("Merchant not found", error.getMessage());
                    assertEquals("FPP_MERCHANT_NOT_FOUND", ((EntityNotFoundException) error).getErrorCode());
                });
    }
}
package com.github.gluhov.fakepaymentprovider.service;

import com.github.gluhov.fakepaymentprovider.exception.EntityNotFoundException;
import com.github.gluhov.fakepaymentprovider.model.Merchant;
import com.github.gluhov.fakepaymentprovider.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MerchantService {
    private final MerchantRepository merchantRepository;

    public Mono<Merchant> getById(UUID uuid) {
        return merchantRepository.findById(uuid)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Merchant not found", "FPP_MERCHANT_NOT_FOUND")));
    }

    public Mono<Merchant> findByIdAndSecretKey(String merchantId, String secretKey) {
        return merchantRepository.findByMerchantIdAndSecretKey(merchantId, secretKey)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Merchant not found", "FPP_MERCHANT_NOT_FOUND")));
    }
}
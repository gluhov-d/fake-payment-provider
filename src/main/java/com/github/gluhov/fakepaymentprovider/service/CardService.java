package com.github.gluhov.fakepaymentprovider.service;

import com.github.gluhov.fakepaymentprovider.exception.EntityNotFoundException;
import com.github.gluhov.fakepaymentprovider.model.CardData;
import com.github.gluhov.fakepaymentprovider.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;

    public Mono<CardData> findByCardAccountIdAndCardNumber(UUID uuid, String cardNumber) {
        return cardRepository.findCardByAccountIdAAndCardNumber(uuid, cardNumber);
    }

    public Mono<CardData> getById(UUID uuid) {
        return cardRepository.findById(uuid)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Card not found", "FPP_CARD_NOT_FOUND")));
    }

    public Mono<CardData> save(CardData cardData) {
        return cardRepository.save(cardData);
    }
}

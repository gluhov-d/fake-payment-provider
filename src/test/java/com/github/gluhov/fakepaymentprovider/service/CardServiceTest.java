package com.github.gluhov.fakepaymentprovider.service;

import com.github.gluhov.fakepaymentprovider.exception.EntityNotFoundException;
import com.github.gluhov.fakepaymentprovider.model.CardData;
import com.github.gluhov.fakepaymentprovider.repository.CardRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.github.gluhov.fakepaymentprovider.service.CardTestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class CardServiceTest {
    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardService cardService;

    @Test
    @DisplayName("Test get by id functionality")
    void getById() {
        when(cardRepository.findById(CARD_UUID)).thenReturn(Mono.just(cardCustomerData));

        Mono<CardData> result = cardService.getById(CARD_UUID);
        StepVerifier.create(result)
                        .expectNext(cardCustomerData)
                                .verifyComplete();
        verify(cardRepository, times(1)).findById(CARD_UUID);
    }

    @Test
    @DisplayName("Test find card by account id and card number functionality")
    void findByCardAccountIdAndCardNumber() {
        when(cardRepository.findCardByAccountIdAAndCardNumber(CARD_UUID, cardCustomerData.getCardNumber())).thenReturn(Mono.just(cardCustomerData));

        Mono<CardData> result = cardService.findByCardAccountIdAndCardNumber(CARD_UUID, cardCustomerData.getCardNumber());
        StepVerifier.create(result)
                        .expectNext(cardCustomerData)
                                .verifyComplete();
        verify(cardRepository, times(1)).findCardByAccountIdAAndCardNumber(CARD_UUID, cardCustomerData.getCardNumber());
    }

    @Test
    @DisplayName("Test save card functionality")
    void save() {
        when(cardRepository.save(any())).thenReturn(Mono.just(cardCustomerData));

        Mono<CardData> result = cardService.save(any());
        StepVerifier.create(result)
                        .expectNext(cardCustomerData)
                                .verifyComplete();
        verify(cardRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Test get by not found id functionality")
    void getNotFound() {
        when(cardRepository.findById(CARD_NOT_FOUND_UUID)).thenReturn(Mono.empty());

        Mono<CardData> result = cardService.getById(CARD_NOT_FOUND_UUID);
        StepVerifier.create(result)
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof EntityNotFoundException);
                    assertEquals("Card not found", error.getMessage());
                    assertEquals("FPP_CARD_NOT_FOUND", ((EntityNotFoundException) error).getErrorCode());
                }).verify();
        verify(cardRepository, times(1)).findById(CARD_NOT_FOUND_UUID);
    }
}
package com.github.gluhov.fakepaymentprovider.service;

import com.github.gluhov.fakepaymentprovider.dto.TransactionDto;
import com.github.gluhov.fakepaymentprovider.dto.TransactionResponseDto;
import com.github.gluhov.fakepaymentprovider.exception.ApiException;
import com.github.gluhov.fakepaymentprovider.exception.EntityNotFoundException;
import com.github.gluhov.fakepaymentprovider.exception.ProcessingException;
import com.github.gluhov.fakepaymentprovider.mapper.CardDataMapper;
import com.github.gluhov.fakepaymentprovider.mapper.CustomerMapper;
import com.github.gluhov.fakepaymentprovider.mapper.PaymentMethodMapper;
import com.github.gluhov.fakepaymentprovider.mapper.TransactionMapper;
import com.github.gluhov.fakepaymentprovider.model.*;
import com.github.gluhov.fakepaymentprovider.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {
    private static final Long MIN_AMOUNT_PAYOUT = 100L;
    private static final String PAYOUT = "payout";
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final MerchantService merchantService;
    private final TransactionMapper transactionMapper;
    private final PaymentMethodService paymentMethodService;
    private final CustomerMapper customerMapper;
    private final CardDataMapper cardDataMapper;
    private final PaymentMethodMapper paymentMethodMapper;
    private final WebhookService webhookService;
    private final CardService cardService;
    private final CustomerService customerService;

    public Flux<TransactionDto> getBetweenByType(LocalDateTime startDate, LocalDateTime endDate, UUID merchantId, String type) {
        return transactionRepository.getBetweenByType(merchantId, type, startDate, endDate)
                .switchIfEmpty(Mono.empty())
                .flatMap(this::constructTransactionDto);
    }

    public Mono<TransactionDto> getByIdAndType(UUID id, String type) {
        return transactionRepository.getByIdAndType(id, type)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Transaction not found", "FPP_TRANSACTION_NOT_FOUND")))
                .flatMap(this::constructTransactionDto);
    }

    public Flux<Transaction> getAllWithStatusInProgress() {
        return transactionRepository.getAllWithStatusInProgress();
    }

    private Mono<TransactionDto> constructTransactionDto(Transaction transaction) {
        return customerService.getById(transaction.getCustomerId())
                .flatMap(customer -> accountService.getById(customer.getAccountId())
                        .flatMap(account -> cardService.getById(transaction.getCardId())
                                .flatMap(cardData -> paymentMethodService.getById(transaction.getPaymentMethodId())
                                        .flatMap(paymentMethod -> {
                                            TransactionDto transactionDto = transactionMapper.map(transaction);
                                            transactionDto.setTransactionId(transaction.getId());
                                            transactionDto.setPaymentMethod(paymentMethodMapper.map(paymentMethod));
                                            transactionDto.setCustomer(customerMapper.map(customer));
                                            transactionDto.setCardData(cardDataMapper.map(cardData));
                                            return Mono.just(transactionDto);
                                        }))));
    }

    @Transactional
    public Mono<TransactionResponseDto> createTransaction(TransactionDto transactionDto, UUID uuid, String type) {
        Transaction transaction = transactionMapper.map(transactionDto);
        if (type.equals(PAYOUT) && transaction.getAmount() < MIN_AMOUNT_PAYOUT) {
            return Mono.error(new ApiException("Transaction less than min amount", "FPP_PAYOUT_MIN_AMOUNT"));
        }
        transaction.setType(type);
        CardData cardData = cardDataMapper.map(transactionDto.getCardData());
        Customer customer = customerMapper.map(transactionDto.getCustomer());
        PaymentMethod paymentMethod = paymentMethodMapper.map(transactionDto.getPaymentMethod());
        LocalDateTime now = LocalDateTime.now();

        return merchantService.getById(uuid)
                .flatMap(merchant -> paymentMethodService.findByType(paymentMethod.getType())
                        .flatMap(existingPaymentMethod -> customerService.findCustomerByFirstNameAndLastNameAndCountry(customer.getFirstName(), customer.getLastName(), customer.getCountry())
                                .defaultIfEmpty(new Customer())
                                .flatMap(existingCustomer -> {
                                    transaction.setPaymentMethod(existingPaymentMethod);
                                    transaction.setPaymentMethodId(existingPaymentMethod.getId());
                                    if (existingCustomer.isNew()) {
                                        if (type.equals(PAYOUT)) {
                                            return Mono.error(new ApiException("Customer not found", "FPP_CUSTOMER_NOT_FOUND"));
                                        }
                                        return customerService.createNewCustomer(cardData, customer, transaction, uuid, now)
                                                .flatMap(savedCustomer -> processExistingCustomerTransaction(savedCustomer, transaction, cardData, uuid, now));
                                    } else {
                                        return processExistingCustomerTransaction(existingCustomer, transaction, cardData, uuid, now);
                                    }
                                })
                        )
                );
    }

    private Mono<TransactionResponseDto> processExistingCustomerTransaction(Customer existingCustomer, Transaction transaction, CardData cardData, UUID uuid, LocalDateTime now) {
        return accountService.getById(existingCustomer.getAccountId())
                .flatMap(account -> cardService.findByCardAccountIdAndCardNumber(account.getId(), cardData.getCardNumber())
                        .defaultIfEmpty(new CardData())
                        .flatMap(existingCardData -> {
                            if (existingCardData.isNew()) {
                                if (transaction.getType().equals(PAYOUT)) {
                                    return Mono.error(new ApiException("Wrong card number", "FPP_WRONG_CARD_NUMBER"));
                                } else {
                                    cardData.setAccount_id(existingCustomer.getAccountId());
                                    return cardService.save(cardData)
                                            .flatMap(savedCardData -> saveTransactionAndSentNotification(existingCustomer, transaction, uuid, now, account, savedCardData));
                                }
                            }
                            return saveTransactionAndSentNotification(existingCustomer, transaction, uuid, now, account, existingCardData);
                        }));
    }

    private Mono<TransactionResponseDto> saveTransactionAndSentNotification(Customer existingCustomer, Transaction transaction, UUID uuid, LocalDateTime now, Account account, CardData existingCardData) {
        if (account.getCurrency().equals(transaction.getCurrency())) {
            transaction.setTransactionStatus(TransactionStatus.IN_PROGRESS);
            transaction.setCustomerId(existingCustomer.getId());
            transaction.setCreatedAt(now);
            transaction.setUpdatedAt(now);
            transaction.setCardId(existingCardData.getId());
            transaction.setCreatedBy(String.valueOf(uuid));
            transaction.setModifiedBy(String.valueOf(uuid));
            transaction.setStatus(Status.ACTIVE);
            transaction.setMessage("OK");
            transaction.setMerchantId(uuid);
            return transactionRepository.save(transaction)
                    .flatMap(savedTransaction -> webhookService.sendNotification(savedTransaction)
                            .then(Mono.just(new TransactionResponseDto(savedTransaction.getId(), "OK", savedTransaction.getTransactionStatus()))));
        } else {
            return Mono.error(new ApiException("Wrong account currency", "FPP_WRONG_CURRENCY"));
        }
    }

    public Mono<Transaction> updateTransactionStatus(UUID uuid, TransactionStatus newStatus) {
        return transactionRepository.findById(uuid)
                .flatMap(t -> {
                    if (t.getTransactionStatus().equals(TransactionStatus.IN_PROGRESS)) {
                        t.setTransactionStatus(newStatus);
                        t.setUpdatedAt(LocalDateTime.now());
                        log.debug("Transaction status updated to " + newStatus);
                        return transactionRepository.save(t);
                    } else {
                        return Mono.error(new ProcessingException("Transaction status can not be changed", "FPP_PROCESSING_STATUS_NOT_CHANGED"));
                    }
                });
    }
}
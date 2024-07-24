package com.github.gluhov.fakepaymentprovider.service;

import com.github.gluhov.fakepaymentprovider.dto.TransactionDto;
import com.github.gluhov.fakepaymentprovider.dto.TransactionResponseDto;
import com.github.gluhov.fakepaymentprovider.exception.ApiException;
import com.github.gluhov.fakepaymentprovider.exception.EntityNotFoundException;
import com.github.gluhov.fakepaymentprovider.mapper.CardDataMapper;
import com.github.gluhov.fakepaymentprovider.mapper.CustomerMapper;
import com.github.gluhov.fakepaymentprovider.mapper.PaymentMethodMapper;
import com.github.gluhov.fakepaymentprovider.mapper.TransactionMapper;
import com.github.gluhov.fakepaymentprovider.model.*;
import com.github.gluhov.fakepaymentprovider.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final MerchantRepository merchantRepository;
    private final CardRepository cardRepository;
    private final TransactionMapper transactionMapper;
    private final CustomerMapper customerMapper;
    private final CardDataMapper cardDataMapper;
    private final PaymentMethodMapper paymentMethodMapper;

    public Flux<TransactionDto> getBetween(LocalDateTime startDate, LocalDateTime endDate, UUID merchantId) {
        return transactionRepository.getBetween(merchantId, startDate, endDate)
                .flatMap(this::constructTransactionDto);
    }

    public Mono<TransactionDto> getById(UUID id) {
        return transactionRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Transaction not found", "FPP_TRANSACTION_NOT_FOUND")))
                .flatMap(this::constructTransactionDto);
    }

    private Mono<TransactionDto> constructTransactionDto(Transaction transaction) {
        return customerRepository.findById(transaction.getCustomerId())
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Customer not found", "FPP_CUSTOMER_NOT_FOUND")))
                .flatMap(customer -> accountRepository.findById(customer.getAccountId())
                        .switchIfEmpty(Mono.error(new EntityNotFoundException("Account not found", "FPP_ACCOUNT_NOT_FOUND")))
                        .flatMap(account -> cardRepository.findById(account.getCardId())
                                .switchIfEmpty(Mono.error(new EntityNotFoundException("Card data not found", "FPP_CARD_DATA_NOT_FOUND")))
                                .flatMap(cardData -> paymentMethodRepository.findById(transaction.getPaymentMethodId())
                                        .switchIfEmpty(Mono.error((new EntityNotFoundException("Payment method not found", "FPP_PAYMENT_METHOD_NOT_FOUND"))))
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
    public Mono<?> topUp(TransactionDto transactionDto, UUID uuid) {
        Transaction transaction = transactionMapper.map(transactionDto);
        CardData cardData = cardDataMapper.map(transactionDto.getCardData());
        Customer customer = customerMapper.map(transactionDto.getCustomer());
        PaymentMethod paymentMethod = paymentMethodMapper.map(transactionDto.getPaymentMethod());
        LocalDateTime now = LocalDateTime.now();

        return merchantRepository.findById(uuid)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Merchant not found", "FPP_MERCHANT_NOT_FOUND")))
                .flatMap(merchant -> paymentMethodRepository.findPaymentMethodByType(paymentMethod.getType())
                        .switchIfEmpty(Mono.error(new ApiException("Payment method not found", "FPP_PAYMENT_METHOD_NOT_ALLOWED")))
                        .flatMap(existingPaymentMethod -> customerRepository.findCustomerByFirstNameAndLastName(customer.getFirstName(), customer.getLastName())
                                .defaultIfEmpty(new Customer())
                                .flatMap(existingCustomer -> {
                                    transaction.setPaymentMethod(existingPaymentMethod);
                                    transaction.setPaymentMethodId(existingPaymentMethod.getId());
                                    if (existingCustomer.getId() == null) {
                                        return createNewCustomerAndTransaction(cardData, customer, transaction, uuid, now);
                                    } else {
                                        return processExistingCustomerTransaction(existingCustomer, transaction, uuid, now);
                                    }
                                })
                        )
                );
    }

    private Mono<?> createNewCustomerAndTransaction(CardData cardData, Customer customer, Transaction transaction, UUID uuid, LocalDateTime now) {
        return cardRepository.save(
                    CardData.builder()
                            .cvv(cardData.getCvv())
                            .expDate(cardData.getExpDate())
                            .cardNumber(cardData.getCardNumber())
                            .createdAt(now)
                            .updatedAt(now)
                            .createdBy(String.valueOf(uuid))
                            .modifiedBy(uuid.toString())
                            .status(Status.ACTIVE)
                            .build())
                .onErrorResume(error -> Mono.error(new RuntimeException(error.getMessage())))
                .flatMap(savedCard -> accountRepository.save(
                                Account.builder()
                                        .balance(0L)
                                        .cardId(savedCard.getId())
                                        .currency(transaction.getCurrency())
                                        .createdAt(now)
                                        .updatedAt(now)
                                        .status(Status.ACTIVE)
                                        .createdBy(String.valueOf(uuid))
                                        .modifiedBy(String.valueOf(uuid))
                                        .build())
                        .onErrorResume(error -> Mono.error(new RuntimeException(error.getMessage())))
                        .flatMap(savedAccount -> customerRepository.save(
                                        Customer.builder()
                                                .firstName(customer.getFirstName())
                                                .lastName(customer.getLastName())
                                                .country(customer.getCountry())
                                                .accountId(savedAccount.getId())
                                                .status(Status.ACTIVE)
                                                .createdAt(now)
                                                .updatedAt(now)
                                                .createdBy(String.valueOf(uuid))
                                                .modifiedBy(String.valueOf(uuid))
                                                .build())
                                .onErrorResume(error -> Mono.error(new RuntimeException(error.getMessage())))
                                .flatMap(savedCustomer -> processExistingCustomerTransaction(savedCustomer, transaction, uuid, now))
                        )
                );
    }

    private Mono<?> processExistingCustomerTransaction(Customer existingCustomer, Transaction transaction, UUID uuid, LocalDateTime now) {
        return accountRepository.findById(existingCustomer.getAccountId())
                .flatMap(account -> {
                    if (account.getCurrency().equals(transaction.getCurrency())) {
                        transaction.setTransactionStatus(TransactionStatus.IN_PROGRESS);
                        transaction.setCustomerId(existingCustomer.getId());
                        transaction.setType("transaction");
                        transaction.setCreatedAt(now);
                        transaction.setUpdatedAt(now);
                        transaction.setCreatedBy(String.valueOf(uuid));
                        transaction.setModifiedBy(String.valueOf(uuid));
                        transaction.setStatus(Status.ACTIVE);
                        transaction.setMessage("OK");
                        transaction.setMerchantId(uuid);
                        return transactionRepository.save(transaction)
                                .flatMap(savedTransaction -> Mono.just(new TransactionResponseDto(savedTransaction.getId(), "OK", savedTransaction.getTransactionStatus())));
                    } else {
                        return Mono.error(new ApiException("Wrong account currency", "FPP_TOPUP_WRONG_CURRENCY"));
                    }
                });
    }
}
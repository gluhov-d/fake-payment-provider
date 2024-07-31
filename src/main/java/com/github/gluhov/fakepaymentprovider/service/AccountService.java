package com.github.gluhov.fakepaymentprovider.service;

import com.github.gluhov.fakepaymentprovider.exception.EntityNotFoundException;
import com.github.gluhov.fakepaymentprovider.exception.ProcessingException;
import com.github.gluhov.fakepaymentprovider.model.Account;
import com.github.gluhov.fakepaymentprovider.model.Transaction;
import com.github.gluhov.fakepaymentprovider.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    public Mono<Account> getById(UUID uuid) {
        return accountRepository.findById(uuid)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Account not found", "FPP_ACCOUNT_NOT_FOUND")));
    }

    public Mono<Account> findByOwnerIdAndType(UUID uuid, String type) {
        return accountRepository.findByOwnerIdAndType(uuid, type)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Account not found", "FPP_ACCOUNT_NOT_FOUND")));
    }

    public Mono<Void> makeMoneyTransfer(Transaction transaction) {
        return findByOwnerIdAndType(transaction.getCustomerId(), "customer")
                .flatMap(customerAccount -> findByOwnerIdAndType(transaction.getMerchantId(), "merchant")
                        .flatMap(merchantAccount -> {
                            Account recipient;
                            Account sender;
                            if (transaction.getType().equals("transaction")) {
                                recipient = merchantAccount;
                                sender = customerAccount;
                            } else {
                                recipient = customerAccount;
                                sender = merchantAccount;
                            }

                            Long balanceSenderAfter = sender.getBalance() - transaction.getAmount();
                            if (balanceSenderAfter < 0) {
                                return Mono.error(new ProcessingException("Cannot save new balance", "FPP_PROCESSING_MONEY_TRANSFER_ERROR"));
                            }
                            sender.setBalance(balanceSenderAfter);

                            return accountRepository.save(sender)
                                    .flatMap(savedSenderAccount -> {
                                        if (!savedSenderAccount.getBalance().equals(balanceSenderAfter)) {
                                            return Mono.error(new ProcessingException("Cannot save new balance", "FPP_PROCESSING_MONEY_TRANSFER_ERROR"));
                                        }

                                        Long balanceRecipientBefore = recipient.getBalance();
                                        recipient.setBalance(balanceRecipientBefore + transaction.getAmount());

                                        return accountRepository.save(recipient)
                                                .flatMap(savedRecipientAccount -> {
                                                    if (!savedRecipientAccount.getBalance().equals(balanceRecipientBefore + transaction.getAmount())) {
                                                        return Mono.error(new ProcessingException("Cannot save new balance", "FPP_PROCESSING_MONEY_TRANSFER_ERROR"));
                                                    }

                                                    return Mono.empty();
                                                });
                                    });
                        }));
    }

}
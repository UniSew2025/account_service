package com.unisew.account_service.services.implementors;

import com.unisew.account_service.enums.PaymentType;
import com.unisew.account_service.enums.Status;
import com.unisew.account_service.models.Account;
import com.unisew.account_service.models.Transaction;
import com.unisew.account_service.models.Wallet;
import com.unisew.account_service.repositories.AccountRepo;
import com.unisew.account_service.repositories.TransactionRepo;
import com.unisew.account_service.repositories.WalletRepo;
import com.unisew.account_service.services.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class TransactionImpl implements TransactionService {

    private final TransactionRepo transactionRepository;
    private final AccountRepo accountRepository;
    private final WalletRepo walletRepository;

    @Override
    @Transactional
    public Transaction createInternalTransaction(Integer senderAccountId, Integer receiverAccountId, long amount, PaymentType paymentType, String note, Integer walletId, Status status) {
        Account sender = null;
        if (senderAccountId != null) {
            try {
                sender = accountRepository.findById(senderAccountId)
                        .orElseThrow(() -> new RuntimeException("Sender account not found with ID: " + senderAccountId));
            } catch (Exception e) {
                log.error("Error finding sender account {}: {}", senderAccountId, e.getMessage());
                throw new RuntimeException("Failed to create transaction: " + e.getMessage(), e);
            }
        }

        Account receiver = null;
        if (receiverAccountId != null) {
            try {
                receiver = accountRepository.findById(receiverAccountId)
                        .orElseThrow(() -> new RuntimeException("Receiver account not found with ID: " + receiverAccountId));
            } catch (Exception e) {
                log.error("Error finding receiver account {}: {}", receiverAccountId, e.getMessage());
                throw new RuntimeException("Failed to create transaction: " + e.getMessage(), e);
            }
        }

        Wallet associatedWallet = null;
        if (walletId != null) {
            try {
                associatedWallet = walletRepository.findById(walletId)
                        .orElseThrow(() -> new RuntimeException("Wallet not found with ID: " + walletId));
            } catch (Exception e) {
                log.error("Error finding associated wallet {}: {}", walletId, e.getMessage());
                throw new RuntimeException("Failed to create transaction: " + e.getMessage(), e);
            }
        }

        try {
            Transaction transaction = Transaction.builder()
                    .sender(sender)
                    .senderName(sender != null ? sender.getEmail() : "System/Bank")
                    .receiver(receiver)
                    .receiverName(receiver != null ? receiver.getEmail() : "System/Bank")
                    .amount(amount)
                    .paymentType(paymentType)
                    .note(note)
                    .creationDate(LocalDate.now())
                    .status(status)
                    .paymentGatewayCode(null)
                    .paymentGatewayMessage(null)
                    .orderId(0)
                    .designRequestId(0)
                    .wallet(associatedWallet)
                    .build();

            return transactionRepository.save(transaction);
        } catch (Exception e) {
            log.error("Error saving new transaction: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save transaction: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Transaction> getTransactionById(Integer id) {
        try {
            return transactionRepository.findById(id);
        } catch (Exception e) {
            log.error("Error retrieving transaction with ID {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<Transaction> getTransactionsByAccountId(Integer accountId) {
        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Account not found with ID: " + accountId));
            return transactionRepository.findBySenderOrReceiver(account, account);
        } catch (RuntimeException e) {
            log.error("Error retrieving account {}: {}", accountId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error retrieving transactions for account ID {}: {}", accountId, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<Transaction> getTransactionsByWalletId(Integer walletId) {
        try {
            Wallet wallet = walletRepository.findById(walletId)
                    .orElseThrow(() -> new RuntimeException("Wallet not found with ID: " + walletId));
            return transactionRepository.findByWallet(wallet);
        } catch (RuntimeException e) {
            log.error("Error retrieving wallet {}: {}", walletId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error retrieving transactions for wallet ID {}: {}", walletId, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional
    public Transaction updateTransactionStatus(Integer transactionId, Status newStatus, String gatewayCode, String gatewayMessage) {
        try {
            Transaction transaction = transactionRepository.findById(transactionId)
                    .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));

            transaction.setStatus(newStatus);
            transaction.setPaymentGatewayCode(gatewayCode);
            transaction.setPaymentGatewayMessage(gatewayMessage);

            return transactionRepository.save(transaction);
        } catch (RuntimeException e) {
            log.error("Error updating transaction status for ID {}: {}", transactionId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error saving updated transaction {}: {}", transactionId, e.getMessage(), e);
            throw new RuntimeException("Failed to update transaction status: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Transaction> getAllTransactions() {
        try {
            return transactionRepository.findAll();
        } catch (Exception e) {
            log.error("Error retrieving all transactions: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve all transactions: " + e.getMessage(), e);
        }
    }
}

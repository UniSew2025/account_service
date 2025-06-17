package com.unisew.account_service.services.implementors;

import com.unisew.account_service.enums.PaymentType;
import com.unisew.account_service.enums.Status;
import com.unisew.account_service.models.Transaction;
import com.unisew.account_service.models.Wallet;
import com.unisew.account_service.repositories.AccountRepo;
import com.unisew.account_service.repositories.WalletRepo;
import com.unisew.account_service.services.TransactionService;
import com.unisew.account_service.services.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletImpl implements WalletService {
    private final WalletRepo walletRepository;
    private final AccountRepo accountRepository;
    private final TransactionService transactionService;


    @Override
    @Transactional
    public Wallet deposit(Integer accountId, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        Wallet wallet = walletRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for account ID: " + accountId));

        wallet.setBalance(wallet.getBalance() + amount);
        Wallet updatedWallet = walletRepository.save(wallet);


        transactionService.createInternalTransaction(
                null,
                accountId,
                amount,
                PaymentType.DEPOSIT,
                "Deposit to wallet",
                wallet.getId(),
                Status.TXN_COMPLETED
        );

        return updatedWallet;
    }

    @Override
    @Transactional
    public Wallet withdraw(Integer accountId, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdraw amount must be positive.");
        }
        Wallet wallet = walletRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for account ID: " + accountId));

        if (wallet.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance.");
        }

        wallet.setBalance(wallet.getBalance() - amount);
        Wallet updatedWallet = walletRepository.save(wallet);


        transactionService.createInternalTransaction(
                accountId,
                null,
                amount,
                PaymentType.WITHDRAW,
                "Withdraw from wallet",
                wallet.getId(),
                Status.TXN_PENDING
        );

        return updatedWallet;
    }

    @Override
    @Transactional
    public List<Transaction> transfer(Integer senderAccountId, Integer receiverAccountId, long amount, String note) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive.");
        }
        if (senderAccountId.equals(receiverAccountId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account.");
        }

        Wallet senderWallet = walletRepository.findById(senderAccountId)
                .orElseThrow(() -> new RuntimeException("Sender wallet not found for account ID: " + senderAccountId));
        Wallet receiverWallet = walletRepository.findById(receiverAccountId)
                .orElseThrow(() -> new RuntimeException("Receiver wallet not found for account ID: " + receiverAccountId));

        if (senderWallet.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance for transfer.");
        }


        senderWallet.setBalance(senderWallet.getBalance() - amount);
        receiverWallet.setBalance(receiverWallet.getBalance() + amount);

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);


        Transaction senderTx = transactionService.createInternalTransaction(
                senderAccountId,
                receiverAccountId,
                amount,
                PaymentType.TRANSFER,
                "Transfer out: " + note,
                senderWallet.getId(),
                Status.TXN_COMPLETED
        );


        Transaction receiverTx = transactionService.createInternalTransaction(
                senderAccountId,
                receiverAccountId,
                amount,
                PaymentType.TRANSFER,
                "Transfer in: " + note,
                receiverWallet.getId(),
                Status.TXN_COMPLETED
        );

        return List.of(senderTx, receiverTx);
    }

    @Override
    public Optional<Wallet> getWalletByAccountId(Integer accountId) {
        return walletRepository.findById(accountId);
    }

    @Override
    public List<Wallet> getAllWallets() {
        try {
            return walletRepository.findAll();
        } catch (Exception e) {
            log.error("Error retrieving all wallets: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve all wallets: " + e.getMessage(), e);
        }
    }
}

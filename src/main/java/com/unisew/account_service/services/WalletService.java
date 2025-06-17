package com.unisew.account_service.services;

import com.unisew.account_service.models.Transaction;
import com.unisew.account_service.models.Wallet;

import java.util.List;
import java.util.Optional;

public interface WalletService {
    Wallet deposit(Integer accountId, long amount);
    Wallet withdraw(Integer accountId, long amount);
    List<Transaction> transfer(Integer senderAccountId, Integer receiverAccountId, long amount, String note);
    Optional<Wallet> getWalletByAccountId(Integer accountId);
    List<Wallet> getAllWallets();
}

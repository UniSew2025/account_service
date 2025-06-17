package com.unisew.account_service.repositories;

import com.unisew.account_service.models.Account;
import com.unisew.account_service.models.Transaction;
import com.unisew.account_service.models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepo extends JpaRepository<Transaction, Integer> {
    List<Transaction> findBySenderOrReceiver(Account sender, Account receiver);
    List<Transaction> findByWallet(Wallet wallet);
}

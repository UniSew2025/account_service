package com.unisew.account_service.services;

import com.unisew.account_service.enums.PaymentType;
import com.unisew.account_service.enums.Status;
import com.unisew.account_service.models.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionService {
    Transaction createInternalTransaction(Integer senderAccountId, Integer receiverAccountId, long amount, PaymentType paymentType, String note, Integer walletId, Status status);
    Optional<Transaction> getTransactionById(Integer id);
    List<Transaction> getTransactionsByAccountId(Integer accountId);
    List<Transaction> getTransactionsByWalletId(Integer walletId);
    Transaction updateTransactionStatus(Integer transactionId, Status newStatus, String gatewayCode, String gatewayMessage);
    List<Transaction> getAllTransactions();
}

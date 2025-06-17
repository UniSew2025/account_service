package com.unisew.account_service.services;

import com.unisew.account_service.enums.Status;
import com.unisew.account_service.models.Account;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    Account createAccount(Account account);
    Account updateAccount(Integer id, Account updatedAccountDetails);
    void deleteAccount(Integer id);
    Optional<Account> getAccountById(Integer id);
    List<Account> getAllAccounts();
    Optional<Account> getAccountByEmail(String email);
    Account updateAccountStatus(Integer id, Status status);
}

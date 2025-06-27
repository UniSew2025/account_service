package com.unisew.account_service.services;

import com.unisew.account_service.enums.Status;
import com.unisew.account_service.models.Account;
import com.unisew.account_service.requests.AccountRequestDTO;
import com.unisew.account_service.responses.AccountResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AccountService {
    AccountResponseDTO createAccount(AccountRequestDTO request);
    AccountResponseDTO updateAccount(Integer id, AccountRequestDTO request);
    void deleteAccount(Integer id);
    Optional<AccountResponseDTO> getAccountById(Integer id);
    List<AccountResponseDTO> getAllAccounts();
    Optional<AccountResponseDTO> getAccountByEmail(String email);
    Account updateAccountStatus(Integer id, Status status);

    Map<String, Object> getAccountById(int id);
}

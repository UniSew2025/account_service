package com.unisew.account_service.services.implementors;

import com.unisew.account_service.enums.Status;
import com.unisew.account_service.models.Account;
import com.unisew.account_service.models.Wallet;
import com.unisew.account_service.repositories.AccountRepo;
import com.unisew.account_service.repositories.WalletRepo;
import com.unisew.account_service.services.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class AccountImpl implements AccountService {
    private final AccountRepo accountRepo;
    private final WalletRepo walletRepo;

    @Override
    @Transactional
    public Account createAccount(Account account) {
        try {
            account.setRegisterDate(LocalDate.now());
            account.setStatus(Status.ACCOUNT_ACTIVE);

            Account savedAccount = accountRepo.save(account);

            Wallet wallet = Wallet.builder()
                    .balance(0)
                    .account(savedAccount)
                    .build();
            walletRepo.save(wallet);

            savedAccount.setWallet(wallet);
            return savedAccount;
        } catch (Exception e) {
            log.error("Error creating account and wallet: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create account: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Account updateAccount(Integer id, Account updatedAccountDetails) {
        try {
            Account existingAccount = accountRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Account not found with ID: " + id));
            if (updatedAccountDetails.getEmail() != null && !updatedAccountDetails.getEmail().isEmpty()) {
                existingAccount.setEmail(updatedAccountDetails.getEmail());
            }

            return accountRepo.save(existingAccount);
        } catch (RuntimeException e) {
            log.error("Error updating account {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error saving updated account {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update account: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deleteAccount(Integer id) {
        try {
            Account account = accountRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Account not found with ID: " + id));
            account.setStatus(Status.ACCOUNT_INACTIVE);
            accountRepo.save(account);
        } catch (RuntimeException e) {
            log.error("Error deleting account {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error saving deleted account status {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete account: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Account> getAccountById(Integer id) {
        try {
            return accountRepo.findById(id);
        } catch (Exception e) {
            log.error("Error retrieving account with ID {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<Account> getAllAccounts() {
        try {
            return accountRepo.findAll();
        } catch (Exception e) {
            log.error("Error retrieving all accounts: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve all accounts: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Account> getAccountByEmail(String email) {
        try {
            return accountRepo.findByEmail(email);
        } catch (Exception e) {
            log.error("Error retrieving account with email {}: {}", email, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public Account updateAccountStatus(Integer id, Status status) {
        try {
            Account account = accountRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Account not found with ID: " + id));
            account.setStatus(status);
            return accountRepo.save(account);
        } catch (RuntimeException e) {
            log.error("Error updating account status {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error saving updated account status {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update account status: " + e.getMessage(), e);
        }
    }
}

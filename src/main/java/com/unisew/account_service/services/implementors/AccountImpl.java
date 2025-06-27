package com.unisew.account_service.services.implementors;

import com.unisew.account_service.enums.Role;
import com.unisew.account_service.enums.Status;
import com.unisew.account_service.models.Account;
import com.unisew.account_service.models.Wallet;
import com.unisew.account_service.repositories.AccountRepo;
import com.unisew.account_service.repositories.WalletRepo;
import com.unisew.account_service.requests.AccountRequestDTO;
import com.unisew.account_service.responses.AccountResponseDTO;
import com.unisew.account_service.services.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class AccountImpl implements AccountService {
    private final AccountRepo accountRepo;
    private final WalletRepo walletRepo;

    @Override
    @Transactional
    public AccountResponseDTO createAccount(AccountRequestDTO request) {
        try {
           Account account = new Account();
            account.setEmail(request.getEmail());
            account.setRole(request.getRole());
            account.setRegisterDate(LocalDate.now());
            account.setStatus(Status.ACCOUNT_ACTIVE);
            Wallet wallet = Wallet.builder()
                    .balance(0)
                    .account(account)
                    .build();
            walletRepo.save(wallet);

            account.setWallet(wallet);
            accountRepo.save(account);

            return mapToResponseDTO(account);
        } catch (Exception e) {
            log.error("Error creating account and wallet: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create account: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public AccountResponseDTO updateAccount(Integer id, AccountRequestDTO request) {
        try {
            Account existingAccount = accountRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Account not found with ID: " + id));
            if (request.getEmail() != null && !request.getEmail().isEmpty()) {
                existingAccount.setEmail(request.getEmail());
            }
            existingAccount.setStatus(Status.valueOf(request.getStatus()));
            existingAccount.setRole(request.getRole());
            accountRepo.save(existingAccount);
            return mapToResponseDTO(existingAccount);
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
    public Optional<AccountResponseDTO> getAccountById(Integer id) {
        try {
            return accountRepo.findById(id).map(this::mapToResponseDTO);
        } catch (Exception e) {
            log.error("Error retrieving account with ID {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<AccountResponseDTO> getAllAccounts() {
        try {
            return accountRepo.findAll().stream().map(this::mapToResponseDTO)
                    .filter(account -> account.getRole() != Role.ADMIN)
                    .toList();
        } catch (Exception e) {
            log.error("Error retrieving all accounts: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve all accounts: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<AccountResponseDTO> getAccountByEmail(String email) {
        try {
            return accountRepo.findByEmail(email).map(this::mapToResponseDTO);
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

    private AccountResponseDTO mapToResponseDTO(Account account) {
        return AccountResponseDTO.builder()
                .id(account.getId())
                .email(account.getEmail())
                .role(account.getRole())
                .registerDate(account.getRegisterDate())
                .status(account.getStatus().getValue())
                .build();
    }

    @Override
    public Map<String, Object> getAccountById(int id) {
        Account account = accountRepo.findById(id).orElse(null);
        if (account == null) {
            return null;
        }
        Map<String, Object> accData = new HashMap<>();
        accData.put("id", account.getId());
        accData.put("email", account.getEmail());
        accData.put("role", account.getRole().getValue().toLowerCase());
        accData.put("registerDate", account.getRegisterDate());
        accData.put("status", account.getStatus().getValue().toLowerCase());
        return accData;
    }
}

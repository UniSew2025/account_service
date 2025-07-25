package com.unisew.account_service.services.implementors;

import com.unisew.account_service.enums.Role;
import com.unisew.account_service.enums.Status;
import com.unisew.account_service.models.Account;
import com.unisew.account_service.models.Wallet;
import com.unisew.account_service.repositories.AccountRepo;
import com.unisew.account_service.repositories.WalletRepo;
import com.unisew.account_service.requests.AccountRequestDTO;
import com.unisew.account_service.requests.CreateAccountRequest;
import com.unisew.account_service.requests.CreateProfileRequest;
import com.unisew.account_service.responses.AccountResponseDTO;
import com.unisew.account_service.responses.ResponseObject;
import com.unisew.account_service.services.AccountService;
import com.unisew.account_service.services.DesignService;
import com.unisew.account_service.services.OrderService;
import com.unisew.account_service.services.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private final DesignService designService;
    private final ProfileService profileService;
    private final OrderService orderService;

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> createAccount(CreateAccountRequest request) {

        if (accountRepo.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    ResponseObject.builder()
                            .message("This email is already registered")
                            .build()
            );
        }
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
        profileService.createProfile(
                CreateProfileRequest.builder()
                        .accountId(account.getId())
                        .avatar("https://37assets.37signals.com/svn/765-default-avatar.png")
                        .name(request.getEmail().replace("@gmail.com", ""))
                        .role(request.getRole().toString().toLowerCase())
                        .build()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseObject.builder()
                        .message("Create account successfully")
                        .build()
        );

    }

    @Override
    @Transactional
    public ResponseEntity<ResponseObject> updateAccount(Integer id, AccountRequestDTO request) {
        Account account = accountRepo.findById(id).orElse(null);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    ResponseObject.builder()
                            .message("Account not found")
                            .build()
            );
        }
        if (request.getPackageIds() != null) {
            boolean isSafe = designService.isSafeToBan(request.getPackageIds());
            if (!isSafe) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        ResponseObject.builder()
                                .message("This account has design which is not completed yet, cannot ban!")
                                .build()
                );
            }
        } else {
            boolean isSafe2 = orderService.isSafeToBan(request.getGarmentId());
            if (!isSafe2) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        ResponseObject.builder()
                                .message("This account has order which is not completed yet, cannot ban!")
                                .build()
                );
            }
        }


        account.setStatus(Status.valueOf("ACCOUNT_" + request.getStatus().toUpperCase()));
        accountRepo.save(account);
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Updated account successfully")
                        .build()
        );
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
    public ResponseEntity<ResponseObject> getAllAccounts() {
        List<AccountResponseDTO> accounts = accountRepo.findAll().stream().map(this::mapToResponseDTO)
                .filter(account -> account.getRole() != Role.ADMIN)
                .toList();
        if (accounts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    ResponseObject.builder()
                            .message("No accounts found")
                            .data(accounts)
                            .build()
            );
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseObject.builder()
                        .message("Successfully")
                        .data(accounts)
                        .build()
        );
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
        Map<String, Object> data = profileService.getProfile(account.getId());
        return AccountResponseDTO.builder()
                .id(account.getId())
                .email(account.getEmail())
                .role(account.getRole())
                .registerDate(account.getRegisterDate())
                .status(account.getStatus().getValue())
                .partner(data.get("partner"))
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

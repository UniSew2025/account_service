package com.unisew.account_service.controllers;

import com.unisew.account_service.enums.Status;
import com.unisew.account_service.models.Account;
import com.unisew.account_service.requests.AccountRequestDTO;
import com.unisew.account_service.responses.AccountResponseDTO;
import com.unisew.account_service.responses.ResponseObject;
import com.unisew.account_service.services.AccountService;
import com.unisew.account_service.utils.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/acc")
@Slf4j
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<ResponseObject> createAccount(@Valid @RequestBody AccountRequestDTO request) {
        try {
            log.info("Creating new account with email: {}", request.getEmail());
            
            Account account = Account.builder()
                    .email(request.getEmail())
                    .role(request.getRole())
                    .build();
            
            Account createdAccount = accountService.createAccount(account);
            AccountResponseDTO response = mapToResponseDTO(createdAccount);
            
            log.info("Account created successfully with ID: {}", createdAccount.getId());
            return ResponseBuilder.build(HttpStatus.CREATED, "Account created successfully", response);
            
        } catch (Exception e) {
            log.error("Error creating account: {}", e.getMessage(), e);
            return ResponseBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create account: " + e.getMessage(), null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getAccountById(@PathVariable Integer id) {
        try {
            log.info("Fetching account with ID: {}", id);
            
            return accountService.getAccountById(id)
                    .map(account -> {
                        AccountResponseDTO response = mapToResponseDTO(account);
                        log.info("Account found with ID: {}", id);
                        return ResponseBuilder.build(HttpStatus.OK, "Account retrieved successfully", response);
                    })
                    .orElseGet(() -> {
                        log.warn("Account not found with ID: {}", id);
                        return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Account not found", null);
                    });
                    
        } catch (Exception e) {
            log.error("Error retrieving account with ID {}: {}", id, e.getMessage(), e);
            return ResponseBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve account: " + e.getMessage(), null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateAccount(@PathVariable Integer id, @Valid @RequestBody AccountRequestDTO request) {
        try {
            log.info("Updating account with ID: {}", id);
            
            Account updatedAccountDetails = Account.builder()
                    .email(request.getEmail())
                    .role(request.getRole())
                    .status(Status.valueOf(request.getStatus()))
                    .build();
            
            Account updatedAccount = accountService.updateAccount(id, updatedAccountDetails);
            AccountResponseDTO response = mapToResponseDTO(updatedAccount);
            
            log.info("Account updated successfully with ID: {}", id);
            return ResponseBuilder.build(HttpStatus.OK, "Account updated successfully", response);
            
        } catch (RuntimeException e) {
            log.error("Error updating account {}: {}", id, e.getMessage());
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, e.getMessage(), null);
        } catch (Exception e) {
            log.error("Error updating account with ID {}: {}", id, e.getMessage(), e);
            return ResponseBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update account: " + e.getMessage(), null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteAccount(@PathVariable Integer id) {
        try {
            log.info("Deleting account with ID: {}", id);
            
            accountService.deleteAccount(id);
            
            log.info("Account deleted successfully with ID: {}", id);
            return ResponseBuilder.build(HttpStatus.OK, "Account deleted successfully", null);
            
        } catch (RuntimeException e) {
            log.error("Error deleting account {}: {}", id, e.getMessage());
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, e.getMessage(), null);
        } catch (Exception e) {
            log.error("Error deleting account with ID {}: {}", id, e.getMessage(), e);
            return ResponseBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete account: " + e.getMessage(), null);
        }
    }

    @GetMapping
    public ResponseEntity<ResponseObject> getAllAccounts() {
        try {
            log.info("Fetching all accounts");
            
            List<Account> accounts = accountService.getAllAccounts();
            List<AccountResponseDTO> responseList = accounts.stream()
                    .map(this::mapToResponseDTO)
                    .collect(Collectors.toList());
            
            log.info("Retrieved {} accounts", accounts.size());
            return ResponseBuilder.build(HttpStatus.OK, "Accounts retrieved successfully", responseList);
            
        } catch (Exception e) {
            log.error("Error retrieving all accounts: {}", e.getMessage(), e);
            return ResponseBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve accounts: " + e.getMessage(), null);
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ResponseObject> getAccountByEmail(@PathVariable String email) {
        try {
            log.info("Fetching account with email: {}", email);
            
            return accountService.getAccountByEmail(email)
                    .map(account -> {
                        AccountResponseDTO response = mapToResponseDTO(account);
                        log.info("Account found with email: {}", email);
                        return ResponseBuilder.build(HttpStatus.OK, "Account retrieved successfully", response);
                    })
                    .orElseGet(() -> {
                        log.warn("Account not found with email: {}", email);
                        return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Account not found", null);
                    });
            
        } catch (Exception e) {
            log.error("Error retrieving account with email {}: {}", email, e.getMessage(), e);
            return ResponseBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve account: " + e.getMessage(), null);
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ResponseObject> updateAccountStatus(@PathVariable Integer id, @RequestParam String status) {
        try {
            log.info("Updating account status for ID: {} to status: {}", id, status);
            
            Status accountStatus;
            try {
                accountStatus = Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.error("Invalid status value: {}", status);
                return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Invalid status value: " + status, null);
            }
            
            Account updatedAccount = accountService.updateAccountStatus(id, accountStatus);
            AccountResponseDTO response = mapToResponseDTO(updatedAccount);
            
            log.info("Account status updated successfully for ID: {}", id);
            return ResponseBuilder.build(HttpStatus.OK, "Account status updated successfully", response);
            
        } catch (RuntimeException e) {
            log.error("Error updating account status for ID {}: {}", id, e.getMessage());
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, e.getMessage(), null);
        } catch (Exception e) {
            log.error("Error updating account status for ID {}: {}", id, e.getMessage(), e);
            return ResponseBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update account status: " + e.getMessage(), null);
        }
    }

    /**
     * Helper method to map Account entity to AccountResponseDTO
     */
    private AccountResponseDTO mapToResponseDTO(Account account) {
        return AccountResponseDTO.builder()
                .id(account.getId())
                .email(account.getEmail())
                .role(account.getRole())
                .registerDate(account.getRegisterDate())
                .status(account.getStatus())
                .build();
    }
}

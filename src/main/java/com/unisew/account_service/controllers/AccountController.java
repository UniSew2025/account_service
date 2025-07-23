package com.unisew.account_service.controllers;

import com.unisew.account_service.enums.Role;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/acc")
@Slf4j
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountResponseDTO> createAccount(@Valid @RequestBody AccountRequestDTO request) {
        AccountResponseDTO responseDTO = accountService.createAccount(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDTO);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountResponseDTO> getAccountById(@PathVariable Integer id) {
       AccountResponseDTO responseDTO = accountService.getAccountById(id).get();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountResponseDTO> updateAccount(@PathVariable Integer id, @Valid @RequestBody AccountRequestDTO request) {
       AccountResponseDTO responseDTO = accountService.updateAccount(id, request);
        if (Objects.isNull(responseDTO)) {
            log.error("Account with ID {} not found for update", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        log.info("Account with ID {} updated successfully", id);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountResponseDTO> getAccountByEmail(@PathVariable String email) {
        return accountService.getAccountByEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

}

package com.unisew.account_service.controllers;

import com.unisew.account_service.requests.AccountRequestDTO;
import com.unisew.account_service.requests.CreateAccountRequest;
import com.unisew.account_service.responses.AccountResponseDTO;
import com.unisew.account_service.responses.ResponseObject;
import com.unisew.account_service.services.AccountService;
import com.unisew.account_service.utils.ResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/acc")
@Slf4j
public class AccountController {
    private final AccountService accountService;

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> createAccount(@RequestBody CreateAccountRequest request) {
        return accountService.createAccount(request);
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
    public ResponseEntity<ResponseObject> updateAccount(@PathVariable(name = "id") Integer id, @RequestBody AccountRequestDTO request) {
       return accountService.updateAccount(id, request);
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

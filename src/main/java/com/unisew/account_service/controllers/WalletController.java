package com.unisew.account_service.controllers;

import com.unisew.account_service.models.Transaction;
import com.unisew.account_service.models.Wallet;
import com.unisew.account_service.requests.WalletRequestDTO;
import com.unisew.account_service.responses.ResponseObject;
import com.unisew.account_service.responses.TransactionResponseDTO;
import com.unisew.account_service.responses.WalletResponseDTO;
import com.unisew.account_service.services.WalletService;
import com.unisew.account_service.utils.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/wallet")
@Slf4j
public class WalletController {
    
    private final WalletService walletService;

    @PostMapping("/deposit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> deposit(@Valid @RequestBody WalletRequestDTO request) {
        try {
            log.info("Processing deposit request for account ID: {} with amount: {}", 
                    request.getSenderAccountId(), request.getAmount());
            
            if (request.getAmount() <= 0) {
                return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Deposit amount must be positive", null);
            }
            
            Wallet updatedWallet = walletService.deposit(request.getSenderAccountId(), request.getAmount());
            WalletResponseDTO response = mapToWalletResponseDTO(updatedWallet);
            
            log.info("Deposit successful for account ID: {}. New balance: {}", 
                    request.getSenderAccountId(), updatedWallet.getBalance());
            return ResponseBuilder.build(HttpStatus.OK, "Deposit successful", response);
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid deposit request: {}", e.getMessage());
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        } catch (RuntimeException e) {
            log.error("Error processing deposit for account ID {}: {}", 
                    request.getSenderAccountId(), e.getMessage());
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, e.getMessage(), null);
        } catch (Exception e) {
            log.error("Error processing deposit: {}", e.getMessage(), e);
            return ResponseBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to process deposit: " + e.getMessage(), null);
        }
    }

    @PostMapping("/withdraw")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> withdraw(@Valid @RequestBody WalletRequestDTO request) {
        try {
            log.info("Processing withdraw request for account ID: {} with amount: {}", 
                    request.getSenderAccountId(), request.getAmount());
            
            if (request.getAmount() <= 0) {
                return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Withdraw amount must be positive", null);
            }
            
            Wallet updatedWallet = walletService.withdraw(request.getSenderAccountId(), request.getAmount());
            WalletResponseDTO response = mapToWalletResponseDTO(updatedWallet);
            
            log.info("Withdraw successful for account ID: {}. New balance: {}", 
                    request.getSenderAccountId(), updatedWallet.getBalance());
            return ResponseBuilder.build(HttpStatus.OK, "Withdraw successful", response);
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid withdraw request: {}", e.getMessage());
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        } catch (RuntimeException e) {
            log.error("Error processing withdraw for account ID {}: {}", 
                    request.getSenderAccountId(), e.getMessage());
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        } catch (Exception e) {
            log.error("Error processing withdraw: {}", e.getMessage(), e);
            return ResponseBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to process withdraw: " + e.getMessage(), null);
        }
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> transfer(@Valid @RequestBody WalletRequestDTO request) {
        try {
            log.info("Processing transfer request from account ID: {} to account ID: {} with amount: {}", 
                    request.getSenderAccountId(), request.getReceiverAccountId(), request.getAmount());
            
            if (request.getAmount() <= 0) {
                return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Transfer amount must be positive", null);
            }
            
            if (request.getSenderAccountId().equals(request.getReceiverAccountId())) {
                return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Cannot transfer to the same account", null);
            }
            
            List<Transaction> transactions = walletService.transfer(
                    request.getSenderAccountId(), 
                    request.getReceiverAccountId(), 
                    request.getAmount(), 
                    request.getNote()
            );
            
            List<TransactionResponseDTO> responseList = transactions.stream()
                    .map(this::mapToTransactionResponseDTO)
                    .collect(Collectors.toList());
            
            log.info("Transfer successful from account ID: {} to account ID: {} with amount: {}", 
                    request.getSenderAccountId(), request.getReceiverAccountId(), request.getAmount());
            return ResponseBuilder.build(HttpStatus.OK, "Transfer successful", responseList);
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid transfer request: {}", e.getMessage());
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        } catch (RuntimeException e) {
            log.error("Error processing transfer: {}", e.getMessage());
            return ResponseBuilder.build(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        } catch (Exception e) {
            log.error("Error processing transfer: {}", e.getMessage(), e);
            return ResponseBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to process transfer: " + e.getMessage(), null);
        }
    }

    @GetMapping("/{accountId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getWalletByAccountId(@PathVariable Integer accountId) {
        try {
            log.info("Fetching wallet for account ID: {}", accountId);
            
            return walletService.getWalletByAccountId(accountId)
                    .map(wallet -> {
                        WalletResponseDTO response = mapToWalletResponseDTO(wallet);
                        log.info("Wallet found for account ID: {}", accountId);
                        return ResponseBuilder.build(HttpStatus.OK, "Wallet retrieved successfully", response);
                    })
                    .orElseGet(() -> {
                        log.warn("Wallet not found for account ID: {}", accountId);
                        return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Wallet not found", null);
                    });
                    
        } catch (Exception e) {
            log.error("Error retrieving wallet for account ID {}: {}", accountId, e.getMessage(), e);
            return ResponseBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve wallet: " + e.getMessage(), null);
        }
    }

    @GetMapping("/{accountId}/balance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getWalletBalance(@PathVariable Integer accountId) {
        try {
            log.info("Fetching wallet balance for account ID: {}", accountId);
            
            return walletService.getWalletByAccountId(accountId)
                    .map(wallet -> {
                        WalletResponseDTO response = mapToWalletResponseDTO(wallet);
                        log.info("Wallet balance retrieved for account ID: {}", accountId);
                        return ResponseBuilder.build(HttpStatus.OK, "Wallet balance retrieved successfully", response);
                    })
                    .orElseGet(() -> {
                        log.warn("Wallet not found for account ID: {}", accountId);
                        return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Wallet not found", null);
                    });
                    
        } catch (Exception e) {
            log.error("Error retrieving wallet balance for account ID {}: {}", accountId, e.getMessage(), e);
            return ResponseBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve wallet balance: " + e.getMessage(), null);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getAllWallets() {
        try {
            log.info("Fetching all wallets");
            
            List<Wallet> wallets = walletService.getAllWallets();
            List<WalletResponseDTO> responseList = wallets.stream()
                    .map(this::mapToWalletResponseDTO)
                    .collect(Collectors.toList());
            
            log.info("Retrieved {} wallets", wallets.size());
            return ResponseBuilder.build(HttpStatus.OK, "Wallets retrieved successfully", responseList);
            
        } catch (Exception e) {
            log.error("Error retrieving all wallets: {}", e.getMessage(), e);
            return ResponseBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve wallets: " + e.getMessage(), null);
        }
    }

    private WalletResponseDTO mapToWalletResponseDTO(Wallet wallet) {
        return WalletResponseDTO.builder()
                .id(wallet.getId())
                .balance(wallet.getBalance())
                .pendingBalance(wallet.getPendingBalance())
                .build();
    }

    private TransactionResponseDTO mapToTransactionResponseDTO(Transaction transaction) {
        return TransactionResponseDTO.builder()
                .id(transaction.getId())
                .senderName(transaction.getSenderName())
                .receiverName(transaction.getReceiverName())
                .amount(transaction.getAmount())
                .paymentType(transaction.getPaymentType())
                .note(transaction.getNote())
                .creationDate(transaction.getCreationDate())
                .status(transaction.getStatus())
                .paymentGatewayCode(transaction.getPaymentGatewayCode())
                .paymentGatewayMessage(transaction.getPaymentGatewayMessage())
                .orderId(transaction.getOrderId())
                .designRequestId(transaction.getDesignRequestId())
                .walletId(transaction.getWallet() != null ? transaction.getWallet().getId() : null)
                .build();
    }
}

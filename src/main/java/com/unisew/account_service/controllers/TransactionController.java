package com.unisew.account_service.controllers;

import com.unisew.account_service.enums.Status;
import com.unisew.account_service.models.Transaction;
import com.unisew.account_service.requests.TransactionDTO;
import com.unisew.account_service.responses.ResponseObject;
import com.unisew.account_service.responses.TransactionResponseDTO;
import com.unisew.account_service.services.TransactionService;
import com.unisew.account_service.utils.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transaction")
@Slf4j
public class TransactionController {
    
    private final TransactionService transactionService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getTransactionById(@PathVariable Integer id) {
        try {
            log.info("Fetching transaction with ID: {}", id);
            
            return transactionService.getTransactionById(id)
                    .map(transaction -> {
                        TransactionResponseDTO response = mapToTransactionResponseDTO(transaction);
                        log.info("Transaction found with ID: {}", id);
                        return ResponseBuilder.build(HttpStatus.OK, "Transaction retrieved successfully", response);
                    })
                    .orElseGet(() -> {
                        log.warn("Transaction not found with ID: {}", id);
                        return ResponseBuilder.build(HttpStatus.NOT_FOUND, "Transaction not found", null);
                    });
                    
        } catch (Exception e) {
            log.error("Error retrieving transaction with ID {}: {}", id, e.getMessage(), e);
            return ResponseBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve transaction: " + e.getMessage(), null);
        }
    }

    @GetMapping("/account/{accountId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getTransactionsByAccountId(@PathVariable Integer accountId) {
        try {
            log.info("Fetching transactions for account ID: {}", accountId);
            
            List<Transaction> transactions = transactionService.getTransactionsByAccountId(accountId);
            List<TransactionResponseDTO> responseList = transactions.stream()
                    .map(this::mapToTransactionResponseDTO)
                    .collect(Collectors.toList());
            
            log.info("Retrieved {} transactions for account ID: {}", transactions.size(), accountId);
            return ResponseBuilder.build(HttpStatus.OK, "Transactions retrieved successfully", responseList);
            
        } catch (RuntimeException e) {
            log.error("Error retrieving transactions for account ID {}: {}", accountId, e.getMessage());
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, e.getMessage(), null);
        } catch (Exception e) {
            log.error("Error retrieving transactions for account ID {}: {}", accountId, e.getMessage(), e);
            return ResponseBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve transactions: " + e.getMessage(), null);
        }
    }

    @GetMapping("/wallet/{walletId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getTransactionsByWalletId(@PathVariable Integer walletId) {
        try {
            log.info("Fetching transactions for wallet ID: {}", walletId);
            
            List<Transaction> transactions = transactionService.getTransactionsByWalletId(walletId);
            List<TransactionResponseDTO> responseList = transactions.stream()
                    .map(this::mapToTransactionResponseDTO)
                    .collect(Collectors.toList());
            
            log.info("Retrieved {} transactions for wallet ID: {}", transactions.size(), walletId);
            return ResponseBuilder.build(HttpStatus.OK, "Transactions retrieved successfully", responseList);
            
        } catch (RuntimeException e) {
            log.error("Error retrieving transactions for wallet ID {}: {}", walletId, e.getMessage());
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, e.getMessage(), null);
        } catch (Exception e) {
            log.error("Error retrieving transactions for wallet ID {}: {}", walletId, e.getMessage(), e);
            return ResponseBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve transactions: " + e.getMessage(), null);
        }
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> updateTransactionStatus(
            @PathVariable Integer id, 
            @Valid @RequestBody TransactionDTO request) {
        try {
            log.info("Updating transaction status for ID: {} to status: {}", id, request.getNewStatus());
            
            Status newStatus;
            try {
                newStatus = Status.valueOf(request.getNewStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                log.error("Invalid status value: {}", request.getNewStatus());
                return ResponseBuilder.build(HttpStatus.BAD_REQUEST, "Invalid status value: " + request.getNewStatus(), null);
            }
            
            Transaction updatedTransaction = transactionService.updateTransactionStatus(
                    id, 
                    newStatus, 
                    request.getPaymentGatewayCode(), 
                    request.getPaymentGatewayMessage()
            );
            
            TransactionResponseDTO response = mapToTransactionResponseDTO(updatedTransaction);
            
            log.info("Transaction status updated successfully for ID: {}", id);
            return ResponseBuilder.build(HttpStatus.OK, "Transaction status updated successfully", response);
            
        } catch (RuntimeException e) {
            log.error("Error updating transaction status for ID {}: {}", id, e.getMessage());
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, e.getMessage(), null);
        } catch (Exception e) {
            log.error("Error updating transaction status for ID {}: {}", id, e.getMessage(), e);
            return ResponseBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update transaction status: " + e.getMessage(), null);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getAllTransactions() {
        try {
            log.info("Fetching all transactions");
            
            List<Transaction> transactions = transactionService.getAllTransactions();
            List<TransactionResponseDTO> responseList = transactions.stream()
                    .map(this::mapToTransactionResponseDTO)
                    .collect(Collectors.toList());
            
            log.info("Retrieved {} transactions", transactions.size());
            return ResponseBuilder.build(HttpStatus.OK, "Transactions retrieved successfully", responseList);
            
        } catch (Exception e) {
            log.error("Error retrieving all transactions: {}", e.getMessage(), e);
            return ResponseBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve transactions: " + e.getMessage(), null);
        }
    }

    @GetMapping("/account/{accountId}/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getTransactionSummaryByAccountId(@PathVariable Integer accountId) {
        try {
            log.info("Fetching transaction summary for account ID: {}", accountId);
            
            List<Transaction> transactions = transactionService.getTransactionsByAccountId(accountId);

            long totalAmount = transactions.stream()
                    .mapToLong(Transaction::getAmount)
                    .sum();
            
            long completedTransactions = transactions.stream()
                    .filter(t -> t.getStatus() == Status.TXN_COMPLETED)
                    .count();
            
            long pendingTransactions = transactions.stream()
                    .filter(t -> t.getStatus() == Status.TXN_PENDING)
                    .count();

            var summary = Map.of(
                "totalTransactions", transactions.size(),
                "totalAmount", totalAmount,
                "completedTransactions", completedTransactions,
                "pendingTransactions", pendingTransactions
            );
            
            log.info("Transaction summary retrieved for account ID: {}", accountId);
            return ResponseBuilder.build(HttpStatus.OK, "Transaction summary retrieved successfully", summary);
            
        } catch (RuntimeException e) {
            log.error("Error retrieving transaction summary for account ID {}: {}", accountId, e.getMessage());
            return ResponseBuilder.build(HttpStatus.NOT_FOUND, e.getMessage(), null);
        } catch (Exception e) {
            log.error("Error retrieving transaction summary for account ID {}: {}", accountId, e.getMessage(), e);
            return ResponseBuilder.build(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve transaction summary: " + e.getMessage(), null);
        }
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
                .itemId(transaction.getItemId())
                .walletId(transaction.getWallet() != null ? transaction.getWallet().getId() : null)
                .build();
    }
}

package com.unisew.account_service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {

    ACCOUNT_ACTIVE("active"),
    ACCOUNT_INACTIVE("inactive"),
    ACCOUNT_SUSPENDED("suspended"), // E.g., for temporary lockout
    ACCOUNT_DELETED("deleted"),     // For soft-delete scenarios

    // Transaction Statuses
    TXN_PENDING("pending"),       // Transaction initiated but not yet completed
    TXN_COMPLETED("completed"),   // Transaction successful
    TXN_FAILED("failed"),         // Transaction failed
    TXN_CANCELLED("cancelled"),   // Transaction was cancelled before completion
    TXN_REFUNDED("refunded"),     // Transaction was refunded
    TXN_PROCESSING("processing"); // Currently being processed by a gateway, etc.



    private final String value;
}

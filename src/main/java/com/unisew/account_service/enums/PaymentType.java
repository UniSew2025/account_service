package com.unisew.account_service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentType {

    SCHOOL_ORDER("Order payment"),
    PLATFORM_DESIGNER("Designer fee payment"),
    DEPOSIT("Deposit to wallet"),
    WITHDRAW("Withdraw from wallet"),
    TRANSFER("Funds transfer between wallets");
    private final String value;
}

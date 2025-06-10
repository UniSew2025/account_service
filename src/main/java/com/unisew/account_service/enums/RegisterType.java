package com.unisew.account_service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RegisterType {

    MANUAL("manual"),
    GOOGLE("google");

    private final String value;
}

package com.unisew.account_service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    ADMIN("admin"),
    DESIGNER("designer"),
    SCHOOL("school"),
    GARMENT_FACTORY("garment");

    private final String value;
}

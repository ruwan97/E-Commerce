package com.rc.ecommerce.model.enums;

import lombok.Getter;

@Getter
public enum CurrencyCode {
    USD(1, "LKR"),
    EUR(2, "USD"),
    LKR(3, "EUR");

    private final int id;
    private final String code;

    CurrencyCode(int id, String code) {
        this.id = id;
        this.code = code;
    }

    public static CurrencyCode getById(int id) {
        for (CurrencyCode currencyCode : values()) {
            if (currencyCode.getId() == id) {
                return currencyCode;
            }
        }
        throw new IllegalArgumentException("Invalid CurrencyCode id: " + id);
    }
}

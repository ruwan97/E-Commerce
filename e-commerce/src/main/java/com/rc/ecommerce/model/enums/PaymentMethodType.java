package com.rc.ecommerce.model.enums;

import lombok.Getter;

@Getter
public enum PaymentMethodType {
    CREDIT_CARD(1, "Credit Card"),
    DEBIT_CARD(2, "Debit Card"),
    PAYPAL(3, "PayPal"),
    BANK_TRANSFER(4, "Bank Transfer"),
    CASH_ON_DELIVERY(5, "Cash on Delivery"),
    DEFAULT(0, "Default");

    private final int id;
    private final String description;

    PaymentMethodType(int id, String description) {
        this.id = id;
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static PaymentMethodType getById(int id) {
        for (PaymentMethodType method : values()) {
            if (method.getId() == id) {
                return method;
            }
        }
        throw new IllegalArgumentException("No payment method found with id " + id);
    }
}

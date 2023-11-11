package com.rc.ecommerce.enums;

public enum PaymentMethodType {
    CREDIT_CARD(1, "Credit Card"),
    DEBIT_CARD(2, "Debit Card"),
    PAYPAL(3, "PayPal"),
    GOOGLE_PAY(4, "Google Pay"),
    APPLE_PAY(5, "Apple Pay"),
    CASH(6, "Cash"),
    BANK_TRANSFER(7, "Bank Transfer"),
    OTHER(8, "Other");

    private final int id;
    private final String description;

    PaymentMethodType(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public static PaymentMethodType getById(int id) {
        for (PaymentMethodType method : PaymentMethodType.values()) {
            if (method.id == id) {
                return method;
            }
        }
        throw new IllegalArgumentException("Invalid PaymentMethod Id: " + id);
    }
}


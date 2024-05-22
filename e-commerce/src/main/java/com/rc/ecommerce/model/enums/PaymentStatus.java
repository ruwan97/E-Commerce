package com.rc.ecommerce.model.enums;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    SUCCESS(1, "Success"),
    PENDING(2, "Pending"),
    CANCELED(3, "Cancelled"),
    FAILED(4, "Failed"),
    CHARGE_BACK(5, "Charged Back");

    private final int id;
    private final String description;

    PaymentStatus(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public static PaymentStatus getById(int id) {
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.id == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid PaymentStatus Id: " + id);
    }
}


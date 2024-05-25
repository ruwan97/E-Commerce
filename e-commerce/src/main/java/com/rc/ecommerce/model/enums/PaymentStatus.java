package com.rc.ecommerce.model.enums;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    SUCCESS(2, "Success"),
    PENDING(0, "Pending"),
    CANCELED(-1, "Cancelled"),
    FAILED(-2, "Failed");

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


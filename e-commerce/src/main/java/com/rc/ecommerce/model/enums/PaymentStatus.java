package com.rc.ecommerce.model.enums;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PENDING(1, "Pending"),
    PAID(2, "Paid"),
    COD(3, "Cash On Delivery"),
    FAILED(4, "Failed"),
    DECLINED(5, "Declined"),
    REFUNDED(6, "Refunded"),
    PARTIALLY_REFUNDED(7, "Partially Refunded");

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


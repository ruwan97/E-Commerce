package com.rc.ecommerce.enums;

public enum PaymentStatus {
    PENDING(1, "Pending"),
    COMPLETED(2, "Completed"),
    DECLINED(3, "Declined"),
    REFUNDED(4, "Refunded");

    private final int id;
    private final String description;

    PaymentStatus(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
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


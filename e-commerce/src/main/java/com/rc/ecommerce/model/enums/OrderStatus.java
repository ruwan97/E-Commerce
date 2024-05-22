package com.rc.ecommerce.model.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING(1, "Pending"),
    SUCCESS(2, "Success"),
    FAILED(3, "Failed"),
    PROCESSING(4, "Processing"),
    SHIPPED(5, "Shipped"),
    DELIVERED(6, "Delivered"),
    CANCELLED(7, "Cancelled");

    private final int id;
    private final String description;

    OrderStatus(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public static OrderStatus getById(int id) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.id == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid OrderStatus Id: " + id);
    }
}


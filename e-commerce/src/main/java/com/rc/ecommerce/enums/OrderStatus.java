package com.rc.ecommerce.enums;

public enum OrderStatus {
    PENDING(1, "Pending"),
    PROCESSING(2, "Processing"),
    SHIPPED(3, "Shipped"),
    DELIVERED(4, "Delivered"),
    CANCELLED(5, "Cancelled");

    private final int id;
    private final String description;

    OrderStatus(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
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


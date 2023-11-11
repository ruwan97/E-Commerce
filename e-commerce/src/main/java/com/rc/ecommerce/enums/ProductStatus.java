package com.rc.ecommerce.enums;

public enum ProductStatus {
    AVAILABLE(1, "Available"),
    OUT_OF_STOCK(2, "Out of Stock"),
    DISCONTINUED(3, "Discontinued"),
    PENDING(4, "Pending"),
    EXPIRED(5, "Expired");

    private final int id;
    private final String description;

    ProductStatus(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public static ProductStatus getById(int id) {
        for (ProductStatus status : ProductStatus.values()) {
            if (status.id == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid ProductStatus Id: " + id);
    }
}


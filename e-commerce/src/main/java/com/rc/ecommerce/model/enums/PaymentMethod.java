package com.rc.ecommerce.model.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    VISA(1, "VISA"),
    MASTER(2, "MASTER");

    private final int id;
    private final String description;

    PaymentMethod(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public static PaymentMethod getById(int id) {
        for (PaymentMethod status : PaymentMethod.values()) {
            if (status.id == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid PaymentMethod Id: " + id);
    }
}

package com.rc.ecommerce.model.enums;

import lombok.Getter;

@Getter
public enum ItemRecurringStatus {
    ACTIVE(1, "Active"),
    CANCELLED(2, "cancelled"),
    COMPLETED(2, "completed");

    private final int id;
    private final String description;

    ItemRecurringStatus(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public static ItemRecurringStatus getById(int id) {
        for (ItemRecurringStatus status : ItemRecurringStatus.values()) {
            if (status.id == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid ItemRecurringStatus Id: " + id);
    }
}

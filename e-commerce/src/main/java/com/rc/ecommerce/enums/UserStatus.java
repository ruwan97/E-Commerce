package com.rc.ecommerce.enums;

public enum UserStatus {
    ACTIVE(1, "Active"),
    INACTIVE(2, "Inactive"),
    SUSPENDED(3, "Suspended"),
    BLOCKED(4, "Blocked"),
    PENDING(5, "Pending"),
    DELETED(6, "Deleted");

    private final int id;
    private final String description;

    UserStatus(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public static UserStatus getById(int id) {
        for (UserStatus status : UserStatus.values()) {
            if (status.id == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid UserStatus Id: " + id);
    }
}


package com.rc.ecommerce.enums;

public enum AuthStatus {
    TRUSTED(1),
    UNTRUSTED(2);

    private final int id;

    AuthStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

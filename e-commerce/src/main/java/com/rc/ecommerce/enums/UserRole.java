package com.rc.ecommerce.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserRole {
    ADMIN(1, "ADMIN"),
    MANAGER(2, "MANAGER"),
    USER(3, "USER");


    @Getter
    private final int id;

    @Getter
    private final String name;

    public static UserRole getById(int id) {
        for (UserRole role : values()) {
            if (role.getId() == id) {
                return role;
            }
        }
        throw new IllegalArgumentException("No matching constant for id: " + id);
    }
}

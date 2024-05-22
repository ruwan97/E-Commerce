package com.rc.ecommerce.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    ADMIN(1, "ADMIN"),
    MANAGER(2, "MANAGER"),
    USER(3, "USER");


    private final int id;

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

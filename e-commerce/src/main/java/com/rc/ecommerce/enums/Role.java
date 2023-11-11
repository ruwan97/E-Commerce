package com.rc.ecommerce.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {
    ADMIN(1, "ADMIN"),
    MANAGER(2, "MANAGER"),
    USER(3, "USER");


    @Getter
    private final int id;

    @Getter
    private final String name;

    public static Role getById(int id) {
        for (Role role : values()) {
            if (role.getId() == id) {
                return role;
            }
        }
        throw new IllegalArgumentException("No matching constant for id: " + id);
    }
}

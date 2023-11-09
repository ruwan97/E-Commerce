package com.rc.ecommerce.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {
    USER(1),
    ADMIN(2),
    MANAGER(3);

    private final int id;

    public int getId() {
        return id;
    }

    public static Role getById(int id) {
        for (Role role : values()) {
            if (role.getId() == id) {
                return role;
            }
        }
        throw new IllegalArgumentException("No matching constant for id: " + id);
    }
}

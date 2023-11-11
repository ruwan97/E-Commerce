package com.rc.ecommerce.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {
    ADMIN_READ(1, "admin:read"),
    ADMIN_UPDATE(2, "admin:update"),
    ADMIN_CREATE(3, "admin:create"),
    ADMIN_DELETE(4, "admin:delete"),
    MANAGER_READ(5, "management:read"),
    MANAGER_UPDATE(6, "management:update"),
    MANAGER_CREATE(7, "management:create"),
    MANAGER_DELETE(8, "management:delete");

    @Getter
    private final int id;

    @Getter
    private final String permission;
}

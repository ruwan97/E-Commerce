package com.rc.ecommerce.model.enums;

import lombok.Getter;

@Getter
public enum AuthStatus {
    TRUSTED(1),
    UNTRUSTED(2);

    private final int id;

    AuthStatus(int id) {
        this.id = id;
    }

}

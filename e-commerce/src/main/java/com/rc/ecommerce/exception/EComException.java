package com.rc.ecommerce.exception;

import lombok.Getter;

@Getter
public class EComException extends Exception {
    private final int statusCode;

    public EComException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

}

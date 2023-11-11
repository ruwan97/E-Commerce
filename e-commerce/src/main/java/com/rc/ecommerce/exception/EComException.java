package com.rc.ecommerce.exception;

public class EComException extends Exception {
    private final int statusCode;

    public EComException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

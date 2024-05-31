package com.rc.ecommerce.model.enums;

import lombok.Getter;

@Getter
public enum MessageType {
    AUTHORIZATION_SUCCESS(1, "Authorization Success"),
    AUTHORIZATION_FAILED(2, "Authorization Failed"),
    RECURRING_INSTALLMENT_SUCCESS(2, "Recurring Installment Success"),
    RECURRING_INSTALLMENT_FAILED(2, "Recurring Installment Failed"),
    RECURRING_COMPLETE(2, "Recurring Complete"),
    RECURRING_STOPPED(2, "Recurring Stopped");

    private final int id;
    private final String description;

    MessageType(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public static MessageType getById(int id) {
        for (MessageType status : MessageType.values()) {
            if (status.id == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid MessageType Id: " + id);
    }
}

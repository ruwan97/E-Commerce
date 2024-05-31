package com.rc.ecommerce.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SavePaymentDto {
    private String orderId;
    private long paymentId;
    private int statusCode;
    private String statusMessage;
    private String cardHolderName;
    private String cardNo;
    private String cardExpiry;
    private String currency;
    private double amount;
    private String customerToken;
}

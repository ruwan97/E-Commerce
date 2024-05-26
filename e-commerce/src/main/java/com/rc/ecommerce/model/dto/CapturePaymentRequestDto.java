package com.rc.ecommerce.model.dto;

import lombok.Data;

@Data
public class CapturePaymentRequestDto {
    private String authorizationToken;
    private double amount;
    private String deductionDetails;
}

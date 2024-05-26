package com.rc.ecommerce.model.dto;

import lombok.Data;

@Data
public class RefundRequestDto {
    private String paymentId;
    private String description;
    private String authorizationToken;
}

package com.rc.ecommerce.model.dto;

import lombok.Data;

import java.util.Map;

@Data
public class CapturePaymentResponse {
    private int status;
    private String msg;
    private Map<String, Object> data;
}

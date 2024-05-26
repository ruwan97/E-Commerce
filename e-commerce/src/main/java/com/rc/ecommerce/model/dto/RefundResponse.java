package com.rc.ecommerce.model.dto;

import lombok.Data;

@Data
public class RefundResponse {
    private int status;
    private String msg;
    private String data;
}

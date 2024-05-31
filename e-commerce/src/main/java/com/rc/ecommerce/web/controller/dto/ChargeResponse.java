package com.rc.ecommerce.web.controller.dto;

import lombok.Data;

@Data
public class ChargeResponse {
    private int status;
    private String msg;
    private ChargeResponseData data;
}

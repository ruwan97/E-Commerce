package com.rc.ecommerce.web.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChargeResponseData {
    @JsonProperty("order_id")
    private String orderId;
    private String items;
    private String currency;
    private double amount;
    @JsonProperty("custom_1")
    private String custom1;
    @JsonProperty("custom_2")
    private String custom2;
    @JsonProperty("payment_id")
    private long paymentId;
    @JsonProperty("status_code")
    private int statusCode;
    @JsonProperty("status_message")
    private String statusMessage;
    private String md5sig;
    @JsonProperty("authorization_token")
    private String authorizationToken;
}

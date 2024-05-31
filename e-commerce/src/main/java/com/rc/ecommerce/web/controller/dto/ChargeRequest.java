package com.rc.ecommerce.web.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ChargeRequest {
    private String type = "PAYMENT";
    @JsonProperty("order_id")
    private String orderId;
    private String items;
    private String currency;
    private double amount;
    @JsonProperty("customer_token")
    private String customerToken;
    @JsonProperty("custom_1")
    private String custom1;
    @JsonProperty("custom_2")
    private String custom2;
    @JsonProperty("notify_url")
    private String notifyUrl;
    @JsonProperty("itemList")
    private List<Item> itemList;
}

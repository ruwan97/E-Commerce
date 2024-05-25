package com.rc.ecommerce.model.dto;

import lombok.Data;

@Data
public class PaymentDTO {
    private String merchantId;
    private String returnUrl;
    private String cancelUrl;
    private String notifyUrl;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String country;
    private String orderId;
    private String items;
    private String currency;
    private double amount;
    private String hash;
}

package com.rc.ecommerce.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PlaceOrderRequestDTO {
    private String orderId;
    private BigDecimal amount;
    private String currency;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String recurrence;
    private String duration;
    private List<OrderItemDTO> items;
}

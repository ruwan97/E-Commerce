package com.rc.ecommerce.model.dto;

import lombok.Data;

@Data
public class OrderItemDTO {
    private Integer productId;
    private int quantity;
}

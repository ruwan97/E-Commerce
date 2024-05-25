package com.rc.ecommerce.model.dto;

import lombok.Data;

import java.util.Map;

@Data
public class PlaceOrderResponseDTO {
    private String url;
    private Map<String, String> params;
}

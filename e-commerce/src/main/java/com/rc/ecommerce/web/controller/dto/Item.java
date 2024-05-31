package com.rc.ecommerce.web.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Item {
    private String name;
    private String number;
    private int quantity;
    @JsonProperty("unit_amount")
    private double unitAmount;
}

package com.rc.ecommerce.model.dto;

import lombok.Data;

@Data
public class AuthRequestDto {
    private String email;
    private String password;
}

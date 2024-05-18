package com.rc.ecommerce.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private AuthBody body;
    private JwtAuth auth;
}

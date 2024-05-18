package com.rc.ecommerce.dto.auth;

import com.rc.ecommerce.enums.AuthStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthBody {
    private AuthStatus authStatus;
    private String userName;
    private Session session;
}

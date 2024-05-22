package com.rc.ecommerce.model.response.auth;

import com.rc.ecommerce.model.enums.AuthStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthBody {
    private AuthStatus authStatus;
    private String userName;
    private Session session;
}

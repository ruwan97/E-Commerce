package com.rc.ecommerce.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

public interface LogoutService {
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication);
}

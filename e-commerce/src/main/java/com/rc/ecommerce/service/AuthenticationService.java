package com.rc.ecommerce.service;

import com.rc.ecommerce.model.dto.AuthRequestDto;
import com.rc.ecommerce.model.response.auth.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AuthenticationService {
    public AuthResponse authenticate(AuthRequestDto request);
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}

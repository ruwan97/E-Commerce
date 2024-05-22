package com.rc.ecommerce.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    public String extractUsername(String token);
    public String generateToken(UserDetails userDetails);
    public String generateRefreshToken(UserDetails userDetails);
    public boolean isTokenValid(String token, UserDetails userDetails);
}

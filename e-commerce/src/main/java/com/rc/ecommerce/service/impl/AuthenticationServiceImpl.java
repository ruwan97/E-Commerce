package com.rc.ecommerce.service.impl;

import com.rc.ecommerce.model.domain.Token;
import com.rc.ecommerce.model.dto.AuthRequestDto;
import com.rc.ecommerce.model.enums.AuthStatus;
import com.rc.ecommerce.model.response.auth.AuthBody;
import com.rc.ecommerce.model.response.auth.AuthResponse;
import com.rc.ecommerce.model.response.auth.JwtAuth;
import com.rc.ecommerce.model.response.auth.Session;
import com.rc.ecommerce.repository.TokenRepository;
import com.rc.ecommerce.model.enums.TokenType;
import com.rc.ecommerce.model.domain.User;
import com.rc.ecommerce.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rc.ecommerce.service.AuthenticationService;
import com.rc.ecommerce.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    @Value("${session.valid.duration}")
    private int sessionValidDuration;

    @Value("${session.renewal.duration}")
    private int sessionRenewalDuration;

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse authenticate(AuthRequestDto request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);

        long currentTime = System.currentTimeMillis();
        long validTo = currentTime + sessionValidDuration;
        long renewAllowedTill = currentTime + sessionRenewalDuration;

        // construct and return the AuthResponse
        return AuthResponse.builder()
                .body(AuthBody.builder()
                        .authStatus(AuthStatus.TRUSTED)
                        .userName(user.getEmail())
                        .session(Session.builder()
                                .id("1")
                                .validFrom(currentTime)
                                .validTo(validTo)
                                .renewAllowedTill(renewAllowedTill)
                                .build())
                        .build())
                .auth(JwtAuth.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build())
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .createdAt(new Date())
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
            token.setExpiredAndRevokedAt(new Date());
        });
        tokenRepository.saveAll(validUserTokens);
    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail).orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = JwtAuth.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}

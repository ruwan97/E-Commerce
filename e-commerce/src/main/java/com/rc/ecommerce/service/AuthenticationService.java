package com.rc.ecommerce.service;

import com.rc.ecommerce.domain.Role;
import com.rc.ecommerce.dto.auth.*;
import com.rc.ecommerce.dto.RegisterRequest;
import com.rc.ecommerce.domain.Token;
import com.rc.ecommerce.enums.AuthStatus;
import com.rc.ecommerce.repository.RoleRepository;
import com.rc.ecommerce.repository.TokenRepository;
import com.rc.ecommerce.enums.TokenType;
import com.rc.ecommerce.domain.User;
import com.rc.ecommerce.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    @Value("${session.valid.duration}")
    private int sessionValidDuration;

    @Value("${session.renewal.duration}")
    private int sessionRenewalDuration;


    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;

    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        Role role = roleRepository.findById(request.getRoleId()).orElseThrow(() -> new IllegalArgumentException("Role not found"));
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();
        return userRepository.save(user);
    }

    public AuthResponse authenticate(AuthRequest request) {
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

    // print user authorities
    private void printUserAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.debug("authentication : {}", authentication);

        if (authentication != null) {
            if (authentication.isAuthenticated()) {
                logger.info("User '{}' Authorities after authentication: {}", authentication.getName(), authentication.getAuthorities());
            } else {
                logger.warn("Authentication is not successful. Authentication details: {}", authentication);
            }
        } else {
            logger.warn("Authentication is null.");
        }
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

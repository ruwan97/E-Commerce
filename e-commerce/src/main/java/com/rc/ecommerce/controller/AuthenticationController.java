package com.rc.ecommerce.controller;

import com.rc.ecommerce.domain.User;
import com.rc.ecommerce.dto.*;
import com.rc.ecommerce.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        RegisterResponse response = new RegisterResponse();
        try {
            User user = authService.register(request);
            response.setUserId(user.getId());
            response.setMessage("Registration Success");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("ERROR {}", e.getMessage());
            response.setUserId(null);
            response.setMessage("An error occurred during user registration : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {
        try {
            AuthenticationResponse authenticationResponse = authService.authenticate(request);
            return ResponseEntity.ok(authenticationResponse);
        } catch (Exception e) {
            logger.error("ERROR {}", e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "An error occurred while user authentication : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            authService.refreshToken(request, response);
        } catch (Exception e) {
            String errorMessage = "An error occurred while refreshing the token: " + e.getMessage();
            System.err.println(errorMessage);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
        }
    }
}

package com.rc.ecommerce.controller;

import com.rc.ecommerce.constants.VersionConstants;
import com.rc.ecommerce.dto.ChangePasswordRequest;
import com.rc.ecommerce.dto.RegistrationRequest;
import com.rc.ecommerce.dto.Response;
import com.rc.ecommerce.exception.EComException;
import com.rc.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping(VersionConstants.APP_API_VERSION + "/user")
@RequiredArgsConstructor
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Response> registerUser(@RequestBody RegistrationRequest request) {
        try {
            logger.debug("UserRegistrationRequest--> {}", request);
            userService.registerUser(request);
            return new ResponseEntity<>(new Response(HttpStatus.OK.value(), "User registered successfully"), HttpStatus.OK);
        } catch (EComException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new Response(e.getStatusCode(), e.getMessage()), HttpStatus.valueOf(e.getStatusCode()));
        }
    }

    @PatchMapping("/reset/password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request, Principal connectedUser) {
        userService.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }
}

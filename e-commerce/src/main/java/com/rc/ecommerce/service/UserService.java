package com.rc.ecommerce.service;

import com.rc.ecommerce.exception.EComException;
import com.rc.ecommerce.model.domain.User;
import com.rc.ecommerce.model.dto.ChangePasswordRequestDto;
import com.rc.ecommerce.model.dto.RegistrationRequestDto;

import java.security.Principal;

public interface UserService {
    public User registerUser(RegistrationRequestDto request) throws EComException;
    public void changePassword(ChangePasswordRequestDto request, Principal connectedUser);
}

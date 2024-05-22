package com.rc.ecommerce.service.impl;

import com.rc.ecommerce.model.domain.Role;
import com.rc.ecommerce.model.domain.User;
import com.rc.ecommerce.model.dto.ChangePasswordRequestDto;
import com.rc.ecommerce.model.dto.RegistrationRequestDto;
import com.rc.ecommerce.model.enums.UserRole;
import com.rc.ecommerce.model.enums.UserStatus;
import com.rc.ecommerce.exception.EComException;
import com.rc.ecommerce.repository.UserRepository;
import com.rc.ecommerce.service.UserService;
import com.rc.ecommerce.util.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final CommonUtils commonUtils;


    @Value("${file.storage.location}")
    private String fileStorageLocation;

    @Override
    public User registerUser(RegistrationRequestDto request) throws EComException {
        // validate request
        validateRegistrationRequest(request);

        // validate unique
        validateUniqueFields(request);

        // Create a new user entity
        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setFirstname(request.getFirstName());
        newUser.setLastname(request.getLastName());
        newUser.setAddress(request.getAddress());
        newUser.setMobile(request.getMobileNo());
        newUser.setCreatedAt(new Date());

        // get user role
        Role role = commonUtils.getUserRole(request.getRoleId());
        newUser.setRole(role);

        // Hash the user's password before saving it
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        newUser.setPassword(encodedPassword);

        newUser.setStatus(UserStatus.ACTIVE.getId());

        // Save the user's profile picture
        try {
            if (!request.isDefaultUser()) {
                // Decode the base64 string to byte array
                byte[] pictureBytes = Base64.getDecoder().decode(request.getBase64Image());
                // Save the image to the server location
                Path path = Paths.get(fileStorageLocation + "/" + generateUniqueFileName() + ".png");
                Files.write(path, pictureBytes);

                // Store the file path
                newUser.setProfileFilePath(path.toString());

                logger.debug("profile picture file path-->{}", newUser.getProfileFilePath());
            }
        } catch (IOException | IllegalArgumentException e) {
            String errorMessage = "Error while processing the profile picture: " + e.getMessage();
            throw new EComException(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMessage);
        }

        // Save the user to the database
        userRepository.save(newUser);
        return newUser;
    }

    private String generateUniqueFileName() {
        String uniqueID = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis();
        return "image_" + timestamp + "_" + uniqueID;
    }

    private void validateUniqueFields(RegistrationRequestDto request) throws EComException {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EComException(HttpStatus.CONFLICT.value(), "Email already exists");
        }

        Role role = commonUtils.getUserRole(request.getRoleId());
        if (role.getId() == UserRole.USER.getId()) {
            if (userRepository.existsByMobile(request.getMobileNo())) {
                throw new EComException(HttpStatus.CONFLICT.value(), "Mobile number already exists");
            }
        }
    }

    private void validateRegistrationRequest(RegistrationRequestDto request) throws EComException {
        if (request == null) {
            throw new EComException(HttpStatus.BAD_REQUEST.value(), "Request cannot be null");
        }

        // get user role
        Role role = commonUtils.getUserRole(request.getRoleId());

        if (role.getId() == UserRole.USER.getId()) {
            if (request.getFirstName() == null || request.getFirstName().isEmpty()) {
                throw new EComException(HttpStatus.BAD_REQUEST.value(), "First name is required for registration");
            }

            if (request.getLastName() == null || request.getLastName().isEmpty()) {
                throw new EComException(HttpStatus.BAD_REQUEST.value(), "Last name is required for registration");
            }

            if (request.getMobileNo() == null || request.getMobileNo().isEmpty()) {
                throw new EComException(HttpStatus.BAD_REQUEST.value(), "Mobile number is required for registration");
            }

            if (!commonUtils.isValidMobileNumber(request.getMobileNo())) {
                throw new EComException(HttpStatus.BAD_REQUEST.value(), "Invalid mobile number");
            }

            if (request.getBase64Image() == null || request.getBase64Image().isEmpty()) {
                throw new EComException(HttpStatus.BAD_REQUEST.value(), "Profile picture is required for registration");
            }
        }

        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new EComException(HttpStatus.BAD_REQUEST.value(), "Email is required for registration");
        }

        if (!commonUtils.isValidEmail(request.getEmail())) {
            throw new EComException(HttpStatus.BAD_REQUEST.value(), "Invalid email address");
        }

        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new EComException(HttpStatus.BAD_REQUEST.value(), "Password is required for registration");
        }

        if (request.getConfirmPassword() == null || request.getConfirmPassword().isEmpty()) {
            throw new EComException(HttpStatus.BAD_REQUEST.value(), "Confirm password is required for registration");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new EComException(HttpStatus.BAD_REQUEST.value(), "Passwords do not match");
        }

        // Password complexity requirements
        if (!commonUtils.isValidPassword(request.getPassword())) {
            throw new EComException(HttpStatus.BAD_REQUEST.value(), "Password does not meet the complexity requirements.");
        }
    }

    @Override
    public void changePassword(ChangePasswordRequestDto request, Principal connectedUser) {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password are not the same");
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        userRepository.save(user);
    }
}

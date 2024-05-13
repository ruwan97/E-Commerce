package com.rc.ecommerce.util;


import com.rc.ecommerce.domain.Role;
import com.rc.ecommerce.enums.UserRole;
import com.rc.ecommerce.exception.EComException;
import com.rc.ecommerce.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CommonUtils {
    private static final Logger logger = LoggerFactory.getLogger(CommonUtils.class);

    @Value("${mobile.number.regex.regexp}")
    private String mobileNumberRegex;

    @Value("${email.regex.regexp}")
    private String emailRegex;

    @Value("${password.regex.regexp}")
    private String passwordRegex;

    private final RoleRepository roleRepository;

    @Autowired
    public CommonUtils(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role getUserRole(int roleId) throws EComException {
        UserRole roleType = UserRole.getById(roleId);
        Optional<Role> optionalRole = roleRepository.findById(roleType.getId());
        Role role = optionalRole.orElseThrow(() -> new EComException(HttpStatus.NOT_FOUND.value(), "Role not found for the given ID"));
        logger.debug("role-->{}", role);
        return role;
    }

    public boolean isValidEmail(String email) {
        logger.debug("email-->{}", email);
        return email.matches(emailRegex);
    }

    public boolean isValidMobileNumber(String mobileNumber) {
        logger.debug("mobileNumber-->{}", mobileNumber);
        return mobileNumber.matches(mobileNumberRegex);
    }

    public boolean isValidPassword(String password) {
        return password.matches(passwordRegex);
    }
}


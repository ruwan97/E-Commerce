package com.rc.ecommerce;

import com.rc.ecommerce.model.domain.User;
import com.rc.ecommerce.model.dto.RegistrationRequestDto;
import com.rc.ecommerce.model.enums.UserRole;
import com.rc.ecommerce.exception.EComException;
import com.rc.ecommerce.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class ECommerceApplication {
    private static final Logger logger = LoggerFactory.getLogger(ECommerceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ECommerceApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(UserService userService) {
        return args -> {
            try {
                var admin = RegistrationRequestDto.builder()
                        .firstName("Admin")
                        .lastName("Admin")
                        .email("admin@gmail.com")
                        .password("Admin@123")
                        .confirmPassword("Admin@123")
                        .roleId(UserRole.ADMIN.getId())
                        .isDefaultUser(true)
                        .build();

                User adminUser = userService.registerUser(admin);
                logger.info("Admin user registered successfully. user id : {}", adminUser.getId());

                var manager = RegistrationRequestDto.builder()
                        .firstName("Manager")
                        .lastName("Manager")
                        .email("manager@gmail.com")
                        .password("Manager@123")
                        .confirmPassword("Manager@123")
                        .roleId(UserRole.MANAGER.getId())
                        .isDefaultUser(true)
                        .build();

                User manaUser = userService.registerUser(manager);
                logger.info("Manager user registered successfully. user id : {}", manaUser.getId());

            } catch (IllegalArgumentException | EComException e) {
                System.err.println("Error during default ADMIN & MANAGER user registration: " + e.getMessage());
            }
        };
    }
}

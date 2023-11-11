package com.rc.ecommerce;

import com.rc.ecommerce.domain.User;
import com.rc.ecommerce.dto.RegisterRequest;
import com.rc.ecommerce.enums.Role;
import com.rc.ecommerce.service.AuthenticationService;
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
    public CommandLineRunner commandLineRunner(AuthenticationService authService) {
        return args -> {
            try {
                var admin = RegisterRequest.builder()
                        .firstname("Admin")
                        .lastname("Admin")
                        .email("admin@gmail.com")
                        .password("admin@123")
                        .roleId(Role.ADMIN.getId())
                        .build();

                User adminUser = authService.register(admin);
                logger.info("Admin user registered successfully. user id : {}", adminUser.getId());

                var manager = RegisterRequest.builder()
                        .firstname("Manager")
                        .lastname("Manager")
                        .email("manager@gmail.com")
                        .password("manager@123")
                        .roleId(Role.MANAGER.getId())
                        .build();

                User manaUser = authService.register(manager);
                logger.info("Manager user registered successfully. user id : {}", manaUser.getId());

            } catch (IllegalArgumentException e) {
                System.err.println("Error during default ADMIN & MANAGER user registration: " + e.getMessage());
            }
        };
    }
}

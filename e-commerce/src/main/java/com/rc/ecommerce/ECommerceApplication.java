package com.rc.ecommerce;

import com.rc.ecommerce.service.AuthenticationService;
import com.rc.ecommerce.dto.RegisterRequest;
import com.rc.ecommerce.enums.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class ECommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ECommerceApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(
            AuthenticationService authService
    ) {
        return args -> {
            try {
                var admin = RegisterRequest.builder()
                        .firstname("Admin")
                        .lastname("Admin")
                        .email("admin@mail.com")
                        .password("password")
                        .roleId(Role.ADMIN.getId())
                        .build();
                System.out.println("Admin token: " + authService.register(admin).getAccessToken());

                var manager = RegisterRequest.builder()
                        .firstname("Admin")
                        .lastname("Admin")
                        .email("manager@mail.com")
                        .password("password")
                        .roleId(Role.MANAGER.getId())
                        .build();
                System.out.println("Manager token: " + authService.register(manager).getAccessToken());
            } catch (IllegalArgumentException e) {
                System.err.println("Error during default ADMIN & MANAGER registration: " + e.getMessage());
            }
        };
    }
}

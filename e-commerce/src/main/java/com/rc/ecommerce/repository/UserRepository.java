package com.rc.ecommerce.repository;

import java.util.Optional;

import com.rc.ecommerce.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}

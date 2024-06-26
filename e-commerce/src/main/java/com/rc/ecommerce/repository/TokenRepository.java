package com.rc.ecommerce.repository;

import java.util.List;
import java.util.Optional;

import com.rc.ecommerce.model.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {

    @Query(value = "SELECT t FROM Token t INNER JOIN User u ON t.user.id = u.id WHERE u.id = :id AND (t.expired = false OR t.revoked = false)")
    List<Token> findAllValidTokenByUser(Integer id);

    Optional<Token> findByToken(String token);

    @Transactional
    @Modifying
    @Query("DELETE FROM Token t WHERE t.expired = true AND t.revoked = true")
    void deleteAllExpiredTokens();
}

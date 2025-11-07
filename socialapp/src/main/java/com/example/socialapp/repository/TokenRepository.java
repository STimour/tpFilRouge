package com.example.socialapp.repository;

import com.example.socialapp.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);
    List<Token> findAllByUserIdAndRevokedFalse(Long userId);
}

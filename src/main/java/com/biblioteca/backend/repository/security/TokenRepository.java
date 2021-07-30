package com.biblioteca.backend.repository.security;

import java.util.Optional;
import com.biblioteca.backend.model.Token.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {

    public Optional<Token> findByToken(String token);

}
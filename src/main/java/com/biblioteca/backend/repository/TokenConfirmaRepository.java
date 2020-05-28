package com.biblioteca.backend.repository;

import java.util.Optional;
import com.biblioteca.backend.model.TokenConfirma;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenConfirmaRepository extends JpaRepository<TokenConfirma, Long> {

    public Optional<TokenConfirma> findByTokenConfirma(String token);

}
package com.biblioteca.backend.service;

import java.util.Optional;
import com.biblioteca.backend.model.TokenConfirma;

public interface ITokenConfirmaService {

    public Optional<TokenConfirma> findByTokenConfirma(String token);

    public TokenConfirma save(TokenConfirma tokenConfirma);

}
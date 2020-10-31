package com.biblioteca.backend.service;

import java.util.Optional;
import com.biblioteca.backend.model.Token;

public interface ITokenService {

    public Optional<Token> findByToken(String token);

    public Token save(Token token);

    public void delete(Long id);

}
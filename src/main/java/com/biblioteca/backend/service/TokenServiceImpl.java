package com.biblioteca.backend.service;

import java.util.Optional;
import com.biblioteca.backend.model.Token;
import com.biblioteca.backend.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TokenServiceImpl implements ITokenService {

    @Autowired
    private TokenRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Token> findByToken(String token) {
        return repository.findByToken(token);
    }

    @Override
    @Transactional
    public Token save(Token token) {
        return repository.save(token);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

}
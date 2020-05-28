package com.biblioteca.backend.service;

import java.util.Optional;
import com.biblioteca.backend.model.TokenConfirma;
import com.biblioteca.backend.repository.TokenConfirmaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TokenConfirmaServiceImpl implements ITokenConfirmaService {

    @Autowired
    private TokenConfirmaRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Optional<TokenConfirma> findByTokenConfirma(String token) {
        return repository.findByTokenConfirma(token);
    }

    @Override
    @Transactional
    public TokenConfirma save(TokenConfirma tokenConfirma) {
        return repository.save(tokenConfirma);
    }

}
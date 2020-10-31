package com.biblioteca.backend.service;

import java.util.Optional;

import com.biblioteca.backend.model.Rol;
import com.biblioteca.backend.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleServiceImpl implements IRoleService {

    @Autowired
    private RolRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Rol> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Rol> findByAuthority(String authority) {
        return repository.findByAuthority(authority);
    }

}
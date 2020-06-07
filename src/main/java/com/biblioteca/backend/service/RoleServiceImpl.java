package com.biblioteca.backend.service;

import java.util.Optional;
import com.biblioteca.backend.model.Role;
import com.biblioteca.backend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleServiceImpl implements IRoleService {

    @Autowired
    private RoleRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findByAuthority(String authority) {
        return repository.findByAuthority(authority);
    }

}
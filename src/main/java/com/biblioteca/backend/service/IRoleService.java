package com.biblioteca.backend.service;

import java.util.Optional;
import com.biblioteca.backend.model.Role;

public interface IRoleService {

    public Optional<Role> findById(Long id);

    public Optional<Role> findByAuthority(String authority);
}
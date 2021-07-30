package com.biblioteca.backend.service;

import java.util.Optional;

import com.biblioteca.backend.model.Rol.Rol;

public interface IRoleService {

    public Optional<Rol> findById(Long id);

    public Optional<Rol> findByAuthority(String authority);
}
package com.biblioteca.backend.repository.security;

import java.util.Optional;

import com.biblioteca.backend.model.Rol.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    public Optional<Rol> findByAuthority(String authority);

}
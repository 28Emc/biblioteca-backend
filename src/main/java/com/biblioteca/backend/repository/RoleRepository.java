package com.biblioteca.backend.repository;

import java.util.Optional;
import com.biblioteca.backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    public Optional<Role> findByAuthority(String authority);

}
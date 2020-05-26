package com.biblioteca.backend.repository;

import java.util.Optional;
import com.biblioteca.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByUsuario(String username);

    Optional<Usuario> findByEmail(String email);

}
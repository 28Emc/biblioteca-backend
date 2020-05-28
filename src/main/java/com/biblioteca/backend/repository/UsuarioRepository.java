package com.biblioteca.backend.repository;

import java.util.Optional;
import com.biblioteca.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    public Optional<Usuario> findByUsuario(String username);

    public Optional<Usuario> findByEmail(String email);

    public Optional<Usuario> findByNroDocumentoAndEmail(String nroDocumento, String email); 

}
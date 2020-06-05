package com.biblioteca.backend.repository;

import java.util.List;
import java.util.Optional;
import com.biblioteca.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    public Optional<Usuario> findByUsuario(String username);

    public Optional<Usuario> findByEmail(String email);

    public Optional<Usuario> findByNroDocumentoAndEmail(String nroDocumento, String email); 

    @Query("select u from Usuario u join fetch u.local l where l.id=?1")
    public List<Usuario> findByLocal(Long id);

}
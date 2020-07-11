package com.biblioteca.backend.repository;

import java.util.List;
import java.util.Optional;
import com.biblioteca.backend.model.Usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    public Optional<Usuario> findByUsuario(String username);

    public Optional<Usuario> findByEmail(String email);

    public Optional<Usuario> findByDniAndEmail(String dni, String email);

    @Query("select u from Usuario u join fetch u.local l where l.id=?1")
    public List<Usuario> findByLocal(Long id);

    @Query("select u from Usuario u join fetch u.rol r where r.authority=?1")
    public List<Usuario> findByRol(String authority);

    @Query("select u from Usuario u join fetch u.rol r where r.authority in ('ROLE_ADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    public List<Usuario> findByRoles();

}
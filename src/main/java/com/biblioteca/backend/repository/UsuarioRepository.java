package com.biblioteca.backend.repository;

import java.util.List;
import java.util.Optional;
import com.biblioteca.backend.model.Usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByUsuario(String username);

    Optional<Usuario> findByEmail(String email);

    @Query("select u from Usuario u where u.dni=?1 and u.email=?2")
    Optional<Usuario> findByDniAndEmail(String dni, String email);

    @Query("select u from Usuario u join fetch u.local l where l.id=?1")
    List<Usuario> findByLocal(Long id);

    @Query("select u from Usuario u join fetch u.rol r where r.authority=?1")
    List<Usuario> findByRol(String authority);

    @Query("select u from Usuario u join fetch u.rol r where r.authority in ('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    List<Usuario> findByRoles();

    @Query("select u from Usuario u join fetch u.rol r join fetch u.local l where r.authority like 'ROLE_ADMIN' and l.id=?1")
    Optional<Usuario> existsAdminInLocal(Long local);

}
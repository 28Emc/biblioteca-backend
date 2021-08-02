package com.biblioteca.backend.repository.security;

import java.util.List;
import java.util.Optional;

import com.biblioteca.backend.model.Persona.Persona;
import com.biblioteca.backend.model.Usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByUsuario(String username);

    List<Usuario> findByPersona(Persona persona);

    @Query("select u from Usuario u join fetch u.persona p where p.nroDocumento=?1 and u.usuario=?2")
    Optional<Usuario> findByDniAndEmail(String dni, String email);

    @Query("select u from Usuario u join fetch u.rol r where r.authority=?1")
    List<Usuario> findByRol(String authority);

    @Query("select u from Usuario u join fetch u.rol r where r.authority in ('ROLE_ADMIN', 'ROLE_EMPLEADO', 'ROLE_USUARIO', 'ROLE_VISITA')")
    List<Usuario> findByRoles();

    //@Query("select u from Usuario u join fetch u.rol r join fetch u.local l where r.authority like 'ROLE_ADMIN' and l.id=?1")
    //Optional<Usuario> existsAdminInLocal(Long local);

}
package com.biblioteca.backend.service;

import java.util.List;
import java.util.Optional;

import com.biblioteca.backend.model.Usuario.DTO.ChangePassword;
import com.biblioteca.backend.model.Usuario.Usuario;

public interface IUsuarioService {

    List<Usuario> findAll();

    Optional<Usuario> findById(Long id);

    Optional<Usuario> findByUsuario(String username);

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByDniAndEmail(String dni, String email);

    List<Usuario> findByLocal(Long idLocal);

    List<Usuario> findByRoles();

    List<Usuario> findByRol(String authority);

    Optional<Usuario> existsAdminInLocal(Long local);

    //Usuario saveUser(Usuario usuario, String tipoOperacion);

    Usuario save(Usuario usuario);

    void delete(Long id);

    Usuario cambiarPassword(ChangePassword dtoPassword) throws Exception;

    Usuario recuperarPassword(ChangePassword dtoPassword) throws Exception;
}
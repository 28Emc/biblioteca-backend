package com.biblioteca.backend.service;

import java.util.List;
import java.util.Optional;

import com.biblioteca.backend.model.Usuario.DTO.ChangePassword;
import com.biblioteca.backend.model.Usuario.Usuario;

public interface IUsuarioService {

    public List<Usuario> findAll();

    public Optional<Usuario> findById(Long id);

    public Optional<Usuario> findByUsuario(String username);

    public Optional<Usuario> findByEmail(String email);

    public Optional<Usuario> findByDniAndEmail(String dni, String email);

    public List<Usuario> findByLocal(Long idLocal);

    public List<Usuario> findByRoles();

    public List<Usuario> findByRol(String authority);

    //public Usuario saveUser(Usuario usuario, String tipoOperacion);

    public Usuario save(Usuario usuario);

    public void delete(Long id);

    public Usuario cambiarPassword(ChangePassword dtoPassword) throws Exception;

    public Usuario recuperarPassword(ChangePassword dtoPassword) throws Exception;
}
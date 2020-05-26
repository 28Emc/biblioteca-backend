package com.biblioteca.backend.service;

import java.util.Optional;
import com.biblioteca.backend.model.Usuario;

public interface IUsuarioService {
    Optional<Usuario> findByUsuario(String username);

    Optional<Usuario> findByEmail(String email);

    Usuario save(Usuario usuario);
}
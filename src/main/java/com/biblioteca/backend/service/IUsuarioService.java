package com.biblioteca.backend.service;

import java.util.List;
import java.util.Optional;
import com.biblioteca.backend.model.Usuario;

public interface IUsuarioService {

    public List<Usuario> findAll();
    
    public Optional<Usuario> findById(Long id);
    
    public Optional<Usuario> findByUsuario(String username);

    public Optional<Usuario> findByEmail(String email);

    public Usuario save(Usuario usuario);

    public void delete(Long id);
}
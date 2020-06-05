package com.biblioteca.backend.service;

import java.util.List;
import java.util.Optional;
import com.biblioteca.backend.model.Usuario;
import com.biblioteca.backend.model.dto.Usuarios.ChangePassword;

public interface IUsuarioService {

    public List<Usuario> findAll();
    
    public Optional<Usuario> findById(Long id);
    
    public Optional<Usuario> findByUsuario(String username);

    public Optional<Usuario> findByEmail(String email);

    public Optional<Usuario> findByNroDocumentoAndEmail(String nroDocumento, String email); 

    public List<Usuario> findByLocal(Long id);

    public Usuario save(Usuario usuario);

    public void delete(Long id);

    public Usuario cambiarPassword(ChangePassword dtoPassword) throws Exception;

    public Usuario recuperarPassword(ChangePassword dtoPassword) throws Exception;
}
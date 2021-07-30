package com.biblioteca.backend.service;

import java.util.List;
import java.util.Optional;

import com.biblioteca.backend.model.Persona.DTO.PersonaDTO;
import com.biblioteca.backend.model.Persona.Persona;
import com.biblioteca.backend.model.Usuario.DTO.ChangePassword;
import com.biblioteca.backend.model.Usuario.Usuario;

public interface IUsuarioService {

    List<Usuario> findAll();

    Optional<Usuario> findById(Long id) throws Exception;

    List<Usuario> findByPersona(Persona persona) throws Exception;

    Optional<Usuario> findByUsuario(String usuario);

    Usuario findByNroDocumentoAndUsuario(String nroDocumento, String usuario) throws Exception;

    List<Usuario> findByRoles();

    List<Usuario> findByRol(String authority);

    //Optional<Usuario> existsAdminInLocal(Long local);

    Usuario save(PersonaDTO personaDTO) throws Exception;

    Usuario activateUser(String token) throws Exception;

    Usuario update(Long id, PersonaDTO personaDTO, Usuario usuarioLogueado) throws Exception;

    Usuario changeUsuarioState(Long idUsuario, boolean tipoOperacion) throws Exception;

    Usuario cambiarPassword(ChangePassword dtoPassword) throws Exception;

    Usuario recuperarPassword(ChangePassword dtoPassword) throws Exception;
}
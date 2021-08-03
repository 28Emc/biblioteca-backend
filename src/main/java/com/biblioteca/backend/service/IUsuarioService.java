package com.biblioteca.backend.service;

import java.util.List;
import java.util.Optional;

import com.biblioteca.backend.model.Persona.DTO.PersonaDTO;
import com.biblioteca.backend.model.Persona.Persona;
import com.biblioteca.backend.model.Token.Token;
import com.biblioteca.backend.model.Usuario.DTO.ChangePassword;
import com.biblioteca.backend.model.Usuario.Usuario;

public interface IUsuarioService {

    List<Usuario> findAllUsers();

    List<PersonaDTO> findAll() throws Exception;

    Optional<Usuario> findById(Long id) throws Exception;

    PersonaDTO findPersonaByIdUsuario(Long id) throws Exception;

    List<Usuario> findByPersona(Persona persona) throws Exception;

    Optional<Usuario> findByUsuario(String usuario);

    Usuario findByNroDocumentoAndUsuario(String nroDocumento, String usuario) throws Exception;

    List<Usuario> findByRoles();

    List<Usuario> findByRol(String authority);

    //Optional<Usuario> existsAdminInLocal(Long local);

    Usuario saveAdmin(PersonaDTO personaDTO) throws Exception;

    Token createTokenAccount(Usuario usuario, String tipoOperacion) throws Exception;

    ChangePassword validateToken(String token) throws Exception;

    Usuario activateUser(String token) throws Exception;

    Usuario update(Long id, PersonaDTO personaDTO, Usuario usuarioLogueado) throws Exception;

    Usuario changeUsuarioState(Long idUsuario, boolean tipoOperacion) throws Exception;

    Usuario cambiarPassword(ChangePassword dtoPassword, String username) throws Exception;

    Usuario recuperarPassword(ChangePassword dtoPassword) throws Exception;
}
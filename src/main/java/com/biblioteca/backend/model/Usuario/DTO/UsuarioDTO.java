package com.biblioteca.backend.model.Usuario.DTO;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class UsuarioDTO {

    private Long id;

    @Column(name = "id_persona")
    @NotNull(message = "{notNull.usuarioDTO.idPersona}")
    private Long idPersona;

    @Column(name = "id_rol")
    @NotNull(message = "{notNull.usuarioDTO.idRol}")
    private Long idRol;

    /* TODO: REVISAR SI SE PUEDE QUITAR, YA QUE EN EMPLEADO YA EXISTE ID LOCAL */
    @Column(name = "id_local")
    private Long idLocal;

    @NotEmpty(message = "{notEmpty.usuarioDTO.usuario}")
    @Size(min = 5, max = 30, message = "{size.usuarioDTO.usuario}")
    private String usuario;

    @NotEmpty(message = "{notEmpty.usuarioDTO.password}")
    @Size(min = 5, max = 30, message = "{size.usuarioDTO.password}")
    // TODO:APLICAR PATTERN DE CONTRASEÑA SEGURA
    private String password;

    @Column(name = "foto_usuario")
    private String fotoUsuario;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Long idPersona) {
        this.idPersona = idPersona;
    }

    public Long getIdRol() {
        return idRol;
    }

    public void setIdRol(Long idRol) {
        this.idRol = idRol;
    }

    public Long getIdLocal() {
        return idLocal;
    }

    public void setIdLocal(Long idLocal) {
        this.idLocal = idLocal;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFotoUsuario() {
        return fotoUsuario;
    }

    public void setFotoUsuario(String fotoUsuario) {
        this.fotoUsuario = fotoUsuario;
    }

    public UsuarioDTO() {
    }

    public UsuarioDTO(Long idPersona, Long idRol, Long idLocal, String usuario, String password, String fotoUsuario) {
        this.idPersona = idPersona;
        this.idRol = idRol;
        this.idLocal = idLocal;
        this.usuario = usuario;
        this.password = password;
        this.fotoUsuario = fotoUsuario;
    }

    public UsuarioDTO(Long id, Long idPersona, Long idRol, Long idLocal, String usuario, String password, String fotoUsuario) {
        this.id = id;
        this.idPersona = idPersona;
        this.idRol = idRol;
        this.idLocal = idLocal;
        this.usuario = usuario;
        this.password = password;
        this.fotoUsuario = fotoUsuario;
    }
}

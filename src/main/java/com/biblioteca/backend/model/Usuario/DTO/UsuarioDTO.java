package com.biblioteca.backend.model.Usuario.DTO;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class UsuarioDTO {

    private Long id;

    @NotEmpty(message = "{notEmpty.usuarioDTO.nombres}")
    @Size(min = 5, max = 50, message = "{size.usuarioDTO.nombres}")
    private String nombres;

    @NotEmpty(message = "{notEmpty.usuarioDTO.apellidoMaterno}")
    @Size(min = 5, max = 50, message = "{size.usuarioDTO.apellidoMaterno}")
    private String apellidoMaterno;

    @NotEmpty(message = "{notEmpty.usuarioDTO.apellidoPaterno}")
    @Size(min = 5, max = 50, message = "{size.usuarioDTO.apellidoPaterno}")
    private String apellidoPaterno;

    @NotEmpty(message = "{notEmpty.usuarioDTO.dni}")
    @Size(min = 8, max = 8, message = "{size.usuarioDTO.dni}")
    private String dni;

    @NotEmpty(message = "{notEmpty.usuarioDTO.direccion}")
    @Size(min = 5, max = 100, message = "{size.usuarioDTO.direccion}")
    private String direccion;

    @NotEmpty(message = "{notEmpty.usuarioDTO.celular}")
    @Size(min = 9, max = 9, message = "{size.usuarioDTO.celular}")
    private String celular;

    @NotEmpty(message = "{notEmpty.usuarioDTO.nombres}")
    @Size(min = 5, max = 30, message = "{size.usuarioDTO.email}")
    @Email(message = "{email.usuarioDTO.email}")
    private String email;

    @NotEmpty(message = "{notEmpty.usuarioDTO.usuario}")
    @Size(min = 5, max = 30, message = "{size.usuarioDTO.usuario}")
    private String usuario;

    @NotEmpty(message = "{notEmpty.usuarioDTO.password}")
    @Size(min = 5, max = 30, message = "{size.usuarioDTO.password}")
    // TODO:APLICAR PATTERN DE CONTRASEÑA SEGURA
    private String password;

    private boolean isActivo;

    private String fotoUsuario;

    private Long rol;

    private Long local;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public boolean isActivo() {
        return isActivo;
    }

    public void setActivo(boolean activo) {
        isActivo = activo;
    }

    public String getFotoUsuario() {
        return fotoUsuario;
    }

    public void setFotoUsuario(String fotoUsuario) {
        this.fotoUsuario = fotoUsuario;
    }

    public Long getRol() {
        return rol;
    }

    public void setRol(Long rol) {
        this.rol = rol;
    }

    public Long getLocal() {
        return local;
    }

    public void setLocal(Long local) {
        this.local = local;
    }

    public UsuarioDTO() {
    }

    public UsuarioDTO(Long id, String nombres, String apellidoMaterno, String apellidoPaterno, String dni, String direccion, String celular, String email, String usuario, String password, boolean isActivo, String fotoUsuario, Long rol, Long local) {
        this.id = id;
        this.nombres = nombres;
        this.apellidoMaterno = apellidoMaterno;
        this.apellidoPaterno = apellidoPaterno;
        this.dni = dni;
        this.direccion = direccion;
        this.celular = celular;
        this.email = email;
        this.usuario = usuario;
        this.password = password;
        this.isActivo = isActivo;
        this.fotoUsuario = fotoUsuario;
        this.rol = rol;
        this.local = local;
    }
}

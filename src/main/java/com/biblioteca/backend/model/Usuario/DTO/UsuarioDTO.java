package com.biblioteca.backend.model.Usuario.DTO;

import com.biblioteca.backend.model.Local.Local;
import com.biblioteca.backend.model.Rol;

public class UsuarioDTO {

    // AQUI VAN LAS VALIDACIONES DEL PAQUETE STARTER-VALIDATIONS

    private Long id;

    private String nombres;

    private String apellidoMaterno;

    private String apellidoPaterno;

    private String dni;

    private String direccion;

    private String celular;

    private String email;

    private String usuario;

    private String password;

    private String passwordConfirmacion;

    private boolean isActivo;

    private String fotoUsuario;

    private Rol rol;

    private Local local;

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

    public String getPasswordConfirmacion() {
        return passwordConfirmacion;
    }

    public void setPasswordConfirmacion(String passwordConfirmacion) {
        this.passwordConfirmacion = passwordConfirmacion;
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

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }

    public UsuarioDTO() {
    }

    public UsuarioDTO(Long id, String nombres, String apellidoMaterno, String apellidoPaterno, String dni, String direccion, String celular, String email, String usuario, String password, String passwordConfirmacion, boolean isActivo, String fotoUsuario, Rol rol, Local local) {
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
        this.passwordConfirmacion = passwordConfirmacion;
        this.isActivo = isActivo;
        this.fotoUsuario = fotoUsuario;
        this.rol = rol;
        this.local = local;
    }
}

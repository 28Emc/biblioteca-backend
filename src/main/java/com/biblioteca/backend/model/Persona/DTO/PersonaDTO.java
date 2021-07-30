package com.biblioteca.backend.model.Persona.DTO;

import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class PersonaDTO {

    private Long id;

    @NotNull(message = "{notNull.empleadoDTO.nombre}")
    private String nombre;

    @Column(name = "apellido_paterno", nullable = false)
    @NotNull(message = "{notNull.empleadoDTO.apellidoPaterno}")
    private String apellidoPaterno;

    @Column(name = "apellido_materno", nullable = false)
    @NotNull(message = "{notNull.empleadoDTO.apellidoMaterno}")
    private String apellidoMaterno;

    @Column(name = "tipo_documento", nullable = false)
    @NotNull(message = "{notNull.empleadoDTO.tipoDocumento}")
    private String tipoDocumento;

    @Column(name = "nro_documento", nullable = false)
    @NotNull(message = "{notNull.empleadoDTO.nroDocumento}")
    private String nroDocumento;

    @Column(nullable = false)
    @NotNull(message = "{notNull.empleadoDTO.sexo}")
    private String sexo;

    private String direccion;

    private String celular;

    @Column(name = "fecha_registro", nullable = false)
    @NotNull(message = "{notNull.empleadoDTO.fechaRegistro}")
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "fecha_baja")
    private LocalDateTime fechaBaja;

    @Column(name = "id_rol")
    @NotNull(message = "{notNull.empleadoDTO.idRol}")
    private Long idRol;

    @Column(name = "id_local")
    private Long idLocal;

    @NotEmpty(message = "{notEmpty.empleadoDTO.usuario}")
    @Size(min = 5, max = 30, message = "{size.empleadoDTO.usuario}")
    private String usuario;

    @NotEmpty(message = "{notEmpty.empleadoDTO.password}")
    @Size(min = 5, max = 30, message = "{size.empleadoDTO.password}")
    private String password;

    @Column(name = "foto_usuario")
    private String fotoUsuario;

    private boolean isActivo;

    public PersonaDTO() {
    }

    public PersonaDTO(String nombre, String apellidoPaterno, String apellidoMaterno, String tipoDocumento, String nroDocumento, String sexo, String direccion, String celular, LocalDateTime fechaRegistro, LocalDateTime fechaActualizacion, LocalDateTime fechaBaja, Long idRol, Long idLocal, String usuario, String password, String fotoUsuario, boolean isActivo) {
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.tipoDocumento = tipoDocumento;
        this.nroDocumento = nroDocumento;
        this.sexo = sexo;
        this.direccion = direccion;
        this.celular = celular;
        this.fechaRegistro = fechaRegistro;
        this.fechaActualizacion = fechaActualizacion;
        this.fechaBaja = fechaBaja;
        this.idRol = idRol;
        this.idLocal = idLocal;
        this.usuario = usuario;
        this.password = password;
        this.fotoUsuario = fotoUsuario;
        this.isActivo = isActivo;
    }

    public PersonaDTO(Long id, String nombre, String apellidoPaterno, String apellidoMaterno, String tipoDocumento, String nroDocumento, String sexo, String direccion, String celular, LocalDateTime fechaRegistro, LocalDateTime fechaActualizacion, LocalDateTime fechaBaja, Long idRol, Long idLocal, String usuario, String password, String fotoUsuario) {
        this.id = id;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.tipoDocumento = tipoDocumento;
        this.nroDocumento = nroDocumento;
        this.sexo = sexo;
        this.direccion = direccion;
        this.celular = celular;
        this.fechaRegistro = fechaRegistro;
        this.fechaActualizacion = fechaActualizacion;
        this.fechaBaja = fechaBaja;
        this.idRol = idRol;
        this.idLocal = idLocal;
        this.usuario = usuario;
        this.password = password;
        this.fotoUsuario = fotoUsuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNroDocumento() {
        return nroDocumento;
    }

    public void setNroDocumento(String nroDocumento) {
        this.nroDocumento = nroDocumento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
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

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public LocalDateTime getFechaBaja() {
        return fechaBaja;
    }

    public void setFechaBaja(LocalDateTime fechaBaja) {
        this.fechaBaja = fechaBaja;
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

    public boolean isActivo() {
        return isActivo;
    }

    public void setActivo(boolean activo) {
        isActivo = activo;
    }
}

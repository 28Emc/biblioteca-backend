package com.biblioteca.backend.model.Acceso.DTO;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

public class AccesoDTO {

    private int id;

    @Column(name = "id_sistema")
    @NotNull(message = "{notNull.accesoDTO.idSistema}")
    private Long idSistema;

    @Column(name = "id_usuario")
    @NotNull(message = "{notNull.accesoDTO.idUsuario}")
    private Long idUsuario;

    @Column(name = "ruta_inicial")
    @NotNull(message = "{notNull.accesoDTO.ruta}")
    private String rutaInicial;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getIdSistema() {
        return idSistema;
    }

    public void setIdSistema(Long idSistema) {
        this.idSistema = idSistema;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getRutaInicial() {
        return rutaInicial;
    }

    public void setRutaInicial(String rutaInicial) {
        this.rutaInicial = rutaInicial;
    }

    public AccesoDTO() {
    }

    public AccesoDTO(Long idSistema, Long idUsuario, String rutaInicial) {
        this.idSistema = idSistema;
        this.idUsuario = idUsuario;
        this.rutaInicial = rutaInicial;
    }

    public AccesoDTO(int id, Long idSistema, Long idUsuario, String rutaInicial) {
        this.id = id;
        this.idSistema = idSistema;
        this.idUsuario = idUsuario;
        this.rutaInicial = rutaInicial;
    }
}

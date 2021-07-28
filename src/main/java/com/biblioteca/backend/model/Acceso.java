package com.biblioteca.backend.model;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Entity
@Table(name = "tb_acceso")
public class Acceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "ID autogenerado")
    private int id;

    @Column(name = "id_sistema")
    @ApiModelProperty(notes = "ID de sistema", required = true, example = "1")
    private Long idSistema;

    @Column(name = "id_usuario")
    @ApiModelProperty(notes = "ID de usuario", required = true, example = "1")
    private Long idUsuario;

    @Column(name = "ruta_inicial")
    @ApiModelProperty(notes = "Ruta inicial", required = true, example = "/dashboard")
    private String rutaInicial;

    public Acceso() {
    }

    public Acceso(int id, Long idSistema, Long idUsuario, String rutaInicial) {
        this.id = id;
        this.idSistema = idSistema;
        this.idUsuario = idUsuario;
        this.rutaInicial = rutaInicial;
    }

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
}

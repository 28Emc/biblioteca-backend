package com.biblioteca.backend.model;

import com.biblioteca.backend.model.Usuario.Usuario;
import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_token")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "ID autogenerado")
    private Long id;

    @Column(name = "token", nullable = false)
    @ApiModelProperty(notes = "Token de confirma de solicitud", required = true, example = "edrnb865ui4bf5u4bd2685gcbd56bf787b5fbfbf")
    private String token;

    @Column(name = "fecha_creacion", nullable = false)
    @ApiModelProperty(notes = "Fecha de creación de token", required = true, example = "2020-05-10")
    private LocalDateTime fechaCreacion;

    @Column(name = "tipo_operacion", nullable = false)
    @ApiModelProperty(notes = "Tipo de operación de solicitud de token", required = true, example = "ACTIVAR USER")
    private String tipoOperacion;

    @OneToOne//(targetEntity = Usuario.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "usuario_id")
    private Usuario usuario;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(String tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    @JsonBackReference
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Token() {
    }

    public Token(Usuario usuario, String tOperacion) {
        this.usuario = usuario;
        fechaCreacion = LocalDateTime.now();
        this.tipoOperacion = tOperacion;
        token = UUID.randomUUID().toString();
    }

}
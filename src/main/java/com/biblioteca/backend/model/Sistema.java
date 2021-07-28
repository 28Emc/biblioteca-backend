package com.biblioteca.backend.model;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Entity
@Table(name = "tb_sistema")
public class Sistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "ID autogenerado")
    private Long id;

    @ApiModelProperty(notes = "Nombre del sistema", required = true, example = "Biblioteca SPA")
    private String sistema;

    public Sistema() {
    }

    public Sistema(Long id, String sistema) {
        this.id = id;
        this.sistema = sistema;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }
}

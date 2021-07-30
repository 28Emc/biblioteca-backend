package com.biblioteca.backend.model.Sistema;

import com.biblioteca.backend.model.Empresa.Empresa;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Entity
@Table(name = "tb_sistema")
public class Sistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "ID autogenerado")
    private Long id;

    // SISTEMA(*):EMPRESA(1)
    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa")
    private Empresa empresa;

    @Column(nullable = false)
    @ApiModelProperty(notes = "Nombre del sistema", required = true, example = "Biblioteca SPA")
    private String sistema;

    public Sistema() {
    }

    public Sistema(Long id, Empresa empresa, String sistema) {
        this.id = id;
        this.empresa = empresa;
        this.sistema = sistema;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }
}

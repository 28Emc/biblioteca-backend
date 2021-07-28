package com.biblioteca.backend.model;

import com.biblioteca.backend.model.Local.Local;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "tb_empresa")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "ID Autogenerado")
    private Long id;

    @Column(length = 11, unique = true, nullable = false)
    @ApiModelProperty(notes = "RUC de la empresa", required = true, example = "10431143201")
    private String ruc;

    @Column(name = "razon_social", length = 100, unique = true, nullable = false)
    @ApiModelProperty(notes = "Razón social de la empresa", required = true, example = "Pepito S.A.C.")
    private String razonSocial;

    @Column(nullable = false)
    @ApiModelProperty(notes = "Dirección de la empresa", required = true, example = "Av. Arequipa 456")
    private String direccion;

    // EMPRESA(1):LOCAL(*)
    //@JsonIgnore
    @OneToMany(mappedBy = "empresa")
    private List<Local> locales;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }


    @JsonManagedReference
    public List<Local> getLocales() {
        return locales;
    }

    public void setLocales(List<Local> locales) {
        this.locales = locales;
    }

    public Empresa() {
    }

    public Empresa(Long id, String razonSocial, String ruc, String direccion, List<Local> locales) {
        this.id = id;
        this.razonSocial = razonSocial;
        this.ruc = ruc;
        this.direccion = direccion;
        this.locales = locales;
    }
}
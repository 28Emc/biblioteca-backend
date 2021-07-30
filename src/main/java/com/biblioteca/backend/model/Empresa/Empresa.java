package com.biblioteca.backend.model.Empresa;

import com.biblioteca.backend.model.Sistema.Sistema;
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

    @Column(nullable = false)
    @ApiModelProperty(notes = "RUC de la empresa", required = true, example = "10431143201")
    private String ruc;

    @Column(name = "razon_social", nullable = false)
    @ApiModelProperty(notes = "Razón social de la empresa", required = true, example = "Pepito S.A.C.")
    private String razonSocial;

    @Column(nullable = false)
    @ApiModelProperty(notes = "Dirección de la empresa", required = true, example = "Av. Arequipa 456")
    private String direccion;

    //@JsonIgnore
    @OneToMany(mappedBy = "empresa")
    private List<Sistema> sistemas;

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

    public Empresa() {
    }

    public Empresa(Long id, String razonSocial, String ruc, String direccion) {
        this.id = id;
        this.razonSocial = razonSocial;
        this.ruc = ruc;
        this.direccion = direccion;
    }
}
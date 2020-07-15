package com.biblioteca.backend.model.Local.DTO;

import com.biblioteca.backend.model.Empresa;

public class LocalDTO {

    private Long id;

    private String direccion;

    private String infoAdicional;

    private Long empresa;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getInfoAdicional() {
        return infoAdicional;
    }

    public void setInfoAdicional(String infoAdicional) {
        this.infoAdicional = infoAdicional;
    }

    public Long getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Long empresa) {
        this.empresa = empresa;
    }

    public LocalDTO() {
    }

    public LocalDTO(Long id, String direccion, String infoAdicional, Long empresa) {
        this.id = id;
        this.direccion = direccion;
        this.infoAdicional = infoAdicional;
        this.empresa = empresa;
    }
}

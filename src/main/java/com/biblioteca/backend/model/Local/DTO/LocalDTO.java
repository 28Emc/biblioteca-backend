package com.biblioteca.backend.model.Local.DTO;

import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class LocalDTO {

    private Long id;

    @NotEmpty(message = "{notEmpty.localDTO.direccion}")
    @Size(min = 6, max = 100, message = "{size.localDTO.direccion}")
    private String direccion;

    @Column(name = "id_empresa")
    @NotNull(message = "{notNull.localDTO.idEmpresa}")
    private Long idEmpresa;

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

    public Long getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Long idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public LocalDTO() {
    }

    public LocalDTO(String direccion, Long idEmpresa) {
        this.direccion = direccion;
        this.idEmpresa = idEmpresa;
    }

    public LocalDTO(Long id, String direccion, Long idEmpresa) {
        this.id = id;
        this.direccion = direccion;
        this.idEmpresa = idEmpresa;
    }
}

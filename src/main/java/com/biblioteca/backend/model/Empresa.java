package com.biblioteca.backend.model;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.biblioteca.backend.model.Local.Local;
import com.fasterxml.jackson.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_empresa")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "ID Autogenerado")
    private Long id;

    @Column(name = "razon_social", length = 100, unique = true, nullable = false)
    @ApiModelProperty(notes = "Razón social de la empresa", required = true, example = "Pepito S.A.C.")
    private String razonSocial;

    @Column(length = 11, unique = true, nullable = false)
    @ApiModelProperty(notes = "RUC de la empresa", required = true, example = "10431143201")
    private String ruc;

    @Column(length = 255, nullable = false)
    @ApiModelProperty(notes = "Dirección de la empresa", required = true, example = "Av. Arequipa 456")
    private String direccion;

    @Column(name = "is_activo", nullable = false)
    @ApiModelProperty(notes = "Estado de la empresa", required = true, example = "true")
    private boolean isActivo;

    // EMPRESA(1):LOCAL(*)
    @JsonIgnore
    @OneToMany(mappedBy = "empresa")
    private List<Local> locales;

}
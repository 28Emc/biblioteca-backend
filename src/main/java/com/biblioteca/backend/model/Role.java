package com.biblioteca.backend.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Entity
@Table(name = "tb_roles")
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(notes = "ID autogenerado")
    private Long id;

    @Column(length = 30, nullable = false, unique = true)
    @ApiModelProperty(notes = "Nombre del rol", required = true, example = "ROLE_ADMIN")
    private String authority;

    @OneToOne(mappedBy = "rol")
    private Usuario usuario;

}
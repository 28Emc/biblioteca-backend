package com.biblioteca.backend.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.biblioteca.backend.model.Usuario.Usuario;
import com.fasterxml.jackson.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Entity
@Table(name = "tb_rol")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(notes = "ID autogenerado")
    private Long id;

    @Column(length = 30, nullable = false, unique = true)
    @ApiModelProperty(notes = "Nombre del rol", required = true, example = "ROLE_ADMIN")
    private String authority;

    //@JsonIgnore
    @JsonBackReference
    @OneToOne(mappedBy = "rol")
    private Usuario usuario;

}
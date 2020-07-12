package com.biblioteca.backend.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.biblioteca.backend.model.Usuario.Usuario;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Entity
@Table(name = "tb_rol")
@Getter @Setter
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

    // IMPIDO EL BLOQUE INFINITO DE ROLES QUE MUESTRAN USUARIOS ASOCIADOS A ROLES Y
    // ASI SUCESIVAMENTE, PERO PIERDO EL REGISTRO AL MOMENTO DE CONSULTAR EL USUARIO
    // (MUESTRA EL ROL, PERO YA NO LOS USUARIOS QUE TIENEN ESE ROL EN LA MISMA
    // CONSULTA)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToOne(mappedBy = "rol")
    private Usuario usuario;

}
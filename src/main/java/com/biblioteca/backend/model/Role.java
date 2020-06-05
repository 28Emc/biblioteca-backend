package com.biblioteca.backend.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(notes = "ID autogenerado")
    private Long id;

    @Column(length = 30, nullable = false, unique = true)
    @ApiModelProperty(notes = "Nombre del rol", required = true, example = "ROLE_ADMIN")
    private String authority;

    @JsonIgnore // IMPIDO EL BLOQUE INFINITO DE ROLES QUE MUESTRAN USUARIOS ASOCIADOS A ROLES Y
                // ASI SUCESIVAMENTE, PERO PIERDO EL REGISTRO AL MOMENTO DE CONSULTAR EL USUARIO
                // (MUESTRA EL ROL, PERO YA NO LOS USUARIOS QUE TIENEN ESE ROL EN LA MISMA
                // CONSULTA)
    @OneToOne(mappedBy = "rol")
    private Usuario usuario;

}
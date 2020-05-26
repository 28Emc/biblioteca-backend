package com.biblioteca.backend.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Entity
@Table(name = "tb_usuarios")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(notes = "ID autogenerado")
    private Long id;

    @Column(length = 30, nullable = false)
    @ApiModelProperty(notes = "Nombre del usuario", required = true, example = "Pepito")
    private String nombres;

    @Column(name = "apellido_materno", length = 30, nullable = false)
    @ApiModelProperty(notes = "Apellido materno del usuario", required = true, example = "Paredes")
    private String apellidoMaterno;

    @Column(name = "apellido_paterno", length = 30, nullable = false)
    @ApiModelProperty(notes = "Apellido Paterno del usuario", required = true, example = "Rojas")
    private String apellidoPaterno;

    @Column(length = 8, name = "nro_documento", unique = true, nullable = false)
    @ApiModelProperty(notes = "DNI del usuario", required = true, example = "98765432")
    private String nroDocumento;

    @Column(length = 200, nullable = true)
    @ApiModelProperty(notes = "Dirección del usuario", required = false, example = "Av. Lima 123")
    private String direccion;

    @Column(length = 9, unique = true, nullable = false)
    @ApiModelProperty(notes = "Celular del usuario", required = true, example = "987123654")
    private String celular;

    @Column(name = "email", unique = true, nullable = false)
    @ApiModelProperty(notes = "Email del usuario", required = true, example = "pepe@gmail.com")
    private String email;

    @Column(name = "usuario", length = 30, unique = true, nullable = false)
    @ApiModelProperty(notes = "Alias del usuario", required = true, example = "pepito2020")
    private String usuario;

    @Column(name = "password", length = 255, nullable = false)
    @ApiModelProperty(notes = "Contraseña del usuario", required = true, example = "$2a$10$mpnvIqpwTF6BJNlr4pXwOOCXk7KZiqZftFHt3IxwZ5ODYMfIBtHg6")
    private String password;

    @Column(name = "estado", nullable = false)
    @ApiModelProperty(notes = "Estado del usuario", required = true, example = "true")
    private boolean isActivo;

    @Column(name = "foto_usuario", nullable = true)
    @ApiModelProperty(notes = "Foto del usuario", required = false, example = "pepito2020.png")
    private String fotoUsuario;

    @Column(name = "fecha_registro", nullable = false)
    @Temporal(TemporalType.DATE)
    @ApiModelProperty(notes = "Fecha de creación del usuario", required = true, example = "2020-05-25")
    private Date fechaRegistro;

    @Column(name = "fecha_actualizacion", nullable = true)
    @Temporal(TemporalType.DATE)
    @ApiModelProperty(notes = "Fecha de actualización del usuario", required = true, example = "2020-06-01")
    private Date fechaActualizacion;

    // USER(1):ROLE(1)
    // CAMBIÈ AQUI DE LAZY A EAGER PARA MANEJAR METODOS EN EL LOGOUTSUCCESSHANDLER
    @OneToOne(fetch = FetchType.EAGER)
    private Role rol;

    @PrePersist
    public void prePersist() {
        fechaRegistro = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        fechaActualizacion = new Date();
    }

}
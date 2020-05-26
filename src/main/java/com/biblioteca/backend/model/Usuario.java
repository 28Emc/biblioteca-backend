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
import lombok.Data;

@Entity
@Table(name = "tb_usuarios")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(length = 30, nullable = false)
    private String nombres;

    @Column(name = "apellido_materno", length = 30, nullable = false)
    private String apellidoMaterno;

    @Column(name = "apellido_paterno", length = 30, nullable = false)
    private String apellidoPaterno;

    @Column(length = 8, name = "nro_documento", unique = true, nullable = false)
    private String nroDocumento;

    @Column(length = 200, nullable = true)
    private String direccion;

    @Column(length = 9, unique = true, nullable = false)
    private String celular;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "usuario", length = 30, unique = true, nullable = false)
    private String usuario;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "estado", nullable = false)
    private boolean isActivo;

    @Column(name = "foto_usuario", nullable = true)
    private String fotoUsuario;

    @Column(name = "fecha_registro", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaRegistro;

    @Column(name = "fecha_actualizacion", nullable = true)
    @Temporal(TemporalType.DATE)
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
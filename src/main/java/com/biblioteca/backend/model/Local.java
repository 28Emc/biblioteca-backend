package com.biblioteca.backend.model;

import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_locales")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Local {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(notes = "ID autogenerado")
    private Long id;

    @Column(length = 255, nullable = false, unique = true)
    @ApiModelProperty(notes = "Dirección del local", required = true, example = "Av. Lima 123")
    private String direccion;

    @Column(nullable = true)
    @ApiModelProperty(notes = "Observaciones del local", required = false, example = "Local central")
    private String observaciones;

    @Column(name = "fecha_registro", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @ApiModelProperty(notes = "Fecha de creación del local", required = true, example = "2020-05-25")
    private Date fechaRegistro;

    @Column(name = "fecha_actualizacion", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    @ApiModelProperty(notes = "Fecha de actualización del local", required = false, example = "2020-06-01")
    private Date fechaActualizacion;

    @Column(name = "estado", nullable = false)
    @ApiModelProperty(notes = "Estado del local", required = true, example = "true")
    private boolean isActivo;

    // @JsonIgnore // COMENTO ESTA LINEA PARA PODER REGISTRAR LOCALES Y ASIGNAR UNA
    // EMPRESA, SIN QUE ESTA EMPRESA CARGUE A SUS LOCALES, CREANDO UN BUCLE INFINITO
    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    // LOCAL(1):EMPLEADO(*)
    @JsonIgnore
    @OneToMany(mappedBy = "local")
    private List<Usuario> usuarios;

    @PrePersist
    public void prePersist() {
        isActivo = true;
        fechaRegistro = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        fechaActualizacion = new Date();
    }

}
package com.biblioteca.backend.model;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_token_confirma")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenConfirma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "ID autogenerado")
    private Long id;

    @Column(name = "token_confirma", nullable = false)
    @ApiModelProperty(notes = "Token de confirma de solicitud", required = true, example = "edrnb865ui4bf5u4bd2685gcbd56bf787b5fbfbf")
    private String tokenConfirma;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_creacion", nullable = false)
    @ApiModelProperty(notes = "Fecha de creación de token", required = true, example = "2020-05-10")
    private Date fechaCreacion;

    @Column(name = "tipo_operacion", nullable = false)
    @ApiModelProperty(notes = "Tipo de operación de solicitud de token", required = true, example = "ACTIVAR USER")
    private String tipoOperacion;

    @OneToOne(targetEntity = Usuario.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "usuario_id")
    private Usuario usuario;

    public TokenConfirma(Usuario usuario, String tOperacion) {
        this.usuario = usuario;
        fechaCreacion = new Date();
        this.tipoOperacion = tOperacion;
        tokenConfirma = UUID.randomUUID().toString();
    }

}
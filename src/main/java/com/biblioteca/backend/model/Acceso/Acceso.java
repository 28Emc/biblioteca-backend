package com.biblioteca.backend.model.Acceso;

import com.biblioteca.backend.model.Sistema;
import com.biblioteca.backend.model.Usuario.Usuario;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Entity
@Table(name = "tb_acceso")
public class Acceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "ID autogenerado")
    private int id;

    // ACCESO(*):SISTEMA(1)
    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sistema")
    private Sistema sistema;

    // ACCESO(*):USUARIO(1)
    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "ruta_inicial")
    @ApiModelProperty(notes = "Ruta inicial", required = true, example = "/dashboard")
    private String rutaInicial;

    public Acceso() {
    }

    public Acceso(int id, Sistema sistema, Usuario usuario, String rutaInicial) {
        this.id = id;
        this.sistema = sistema;
        this.usuario = usuario;
        this.rutaInicial = rutaInicial;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Sistema getSistema() {
        return sistema;
    }

    public void setSistema(Sistema sistema) {
        this.sistema = sistema;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getRutaInicial() {
        return rutaInicial;
    }

    public void setRutaInicial(String rutaInicial) {
        this.rutaInicial = rutaInicial;
    }
}

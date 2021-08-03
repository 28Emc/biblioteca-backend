package com.biblioteca.backend.model.Rol;

import com.biblioteca.backend.model.Usuario.Usuario;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "tb_rol")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "ID autogenerado")
    private Long id;

    @Column(length = 30, nullable = false, unique = true)
    @ApiModelProperty(notes = "Nombre del rol", required = true, example = "ROLE_ADMIN")
    private String authority;

    //@JsonBackReference
    //FUNGE DE ONETOMANY
    @OneToMany(mappedBy = "rol")
    private List<Usuario> usuarios;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @JsonManagedReference
    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public Rol() {
    }

    public Rol(Long id, String authority, List<Usuario> usuarios) {
        this.id = id;
        this.authority = authority;
        this.usuarios = usuarios;
    }
}
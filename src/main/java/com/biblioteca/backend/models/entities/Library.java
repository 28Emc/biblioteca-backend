package com.biblioteca.backend.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_library")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Library {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @ApiModelProperty(notes = "library ID")
    private Long id;

    @Column(name = "address", length = 100, unique = true, nullable = false)
    // @ApiModelProperty(notes = "library address", required = true, example = "Av. Lima 123")
    private String address;

    @Column(columnDefinition = "text")
    // @ApiModelProperty(notes = "library address reference", required = true, example = "Cerca a parque zonal")
    private String addressReference;

    @Column(columnDefinition = "text")
    // @ApiModelProperty(notes = "library image reference", required = true,
    // example = "https://www.example.com/library_1.png")
    private String imageReference;

    @Column(nullable = false)
    // @ApiModelProperty(notes = "Library status", required = true, example = "A")
    private String status;

    @Column(name = "creation_date", nullable = false)
    // @ApiModelProperty(notes = "library creation date", required = true, example = "2020-05-25")
    private LocalDateTime creationDate;

    @Column(name = "modification_date")
    // @ApiModelProperty(notes = "library modification date", example = "2020-06-01")
    private LocalDateTime modificationDate;

    /*
    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    // LOCAL(1):EMPLEADO(*)
    //@JsonIgnore
    @OneToMany(mappedBy = "local")
    private List<Usuario> usuarios;

    @JsonBackReference
    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    @JsonManagedReference
    public List<Usuario> getUsuarios() {
        return usuarios;
    }
    */

    @PrePersist
    public void prePersist() {
        status = "A";
        creationDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        modificationDate = LocalDateTime.now();
    }
}
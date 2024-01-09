package com.biblioteca.backend.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_book")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @ApiModelProperty(notes = "Book ID")
    private Long id;

    @Column(name = "isbn", unique = true, nullable = false)
    // @ApiModelProperty(notes = "Book ISBN", required = true, example = "9791234567896")
    private String ISBN;

    @Column(length = 100, nullable = false)
    // @ApiModelProperty(notes = "Book title", required = true, example = "El Camino de los Reyes")
    private String title;

    @Column(length = 100, nullable = false)
    // @ApiModelProperty(notes = "Book author", required = true, example = "Brandon Sanderson")
    private String author;

    @Column(columnDefinition = "text")
    // @ApiModelProperty(notes = "Book synopsis", example = "Libro que hace parte de una trilogía")
    private String synopsis;

    @Column(length = 4, nullable = false)
    // @ApiModelProperty(notes = "Book stock", required = true, example = 1000)
    private Integer stock;

    @Column(columnDefinition = "text", nullable = false)
    // @ApiModelProperty(notes = "Book image", required = true,
    // example = "https://www.example.com/el-camino-de-los-reyes.png")
    private String image;

    @Column(nullable = false)
    // @ApiModelProperty(notes = "Bok status", required = true, example = "A")
    private String status;

    @Column(name = "pubblish_date", nullable = false)
    // @ApiModelProperty(notes = "Book pubblish date", required = true, example = "2020-05-25")
    private LocalDate pubblishDate;

    @Column(name = "creation_date", nullable = false)
    // @ApiModelProperty(notes = "Book creation date", required = true, example = "2020-05-25")
    private LocalDateTime creationDate;

    @Column(name = "modification_date")
    // @ApiModelProperty(notes = "Book modification date", example = "2020-06-01")
    private LocalDateTime modificationDate;

    /*
    // LIBRO(*):LOCAL(1)
    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_id")
    private Local local;

    // LIBRO(1):PRESTAMO(*)
    //@JsonIgnore
    @OneToMany(mappedBy = "libro")
    private List<Prestamo> prestamos;

    // LIBRO(*):CATEGORIA(1)
    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @JsonBackReference
    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }

    //@JsonManagedReference
    @JsonIgnore
    public List<Prestamo> getPrestamos() {
        return prestamos;
    }

    public void setPrestamos(List<Prestamo> prestamos) {
        this.prestamos = prestamos;
    }

    @JsonBackReference
    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
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

    // ALGORITMO PARA DETERMINAR SI EL CODIGO ISBN DEL LIBRO ES VÁLIDO
    public boolean validateIsbn13(String isbn) {
        if (isbn == null) {
            return false;
        }

        //remove any hyphens
        isbn = isbn.replaceAll("-", "");

        //must be a 13 digit ISBN
        if (isbn.length() != 13) {
            return false;
        }

        try {
            int tot = 0;
            for (int i = 0; i < 12; i++) {
                int digit = Integer.parseInt(isbn.substring(i, i + 1));
                tot += (i % 2 == 0) ? digit : digit * 3;
            }

            //checksum must be 0-9. If calculated as 10 then = 0
            int checksum = 10 - (tot % 10);
            if (checksum == 10) {
                checksum = 0;
            }

            return checksum == Integer.parseInt(isbn.substring(12));
        } catch (NumberFormatException nfe) {
            //to catch invalid ISBNs that have non-numeric characters in them
            return false;
        }
    }
}
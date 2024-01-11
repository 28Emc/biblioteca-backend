package com.biblioteca.backend.models.entities;

import com.biblioteca.backend.utils.Utils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @ApiModelProperty(notes = "Book ID")
    private Long id;

    @Column(name = "isbn", length = 20, unique = true, nullable = false)
    // @ApiModelProperty(notes = "Book ISBN", required = true, example = "9791234567896")
    private String ISBN;

    @Column(length = 100, nullable = false)
    // @ApiModelProperty(notes = "Book title", required = true, example = "El Camino de los Reyes")
    private String title;

    @Column(length = 100, nullable = false)
    // @ApiModelProperty(notes = "Book author", required = true, example = "Brandon Sanderson")
    private String author;

    @Column(name = "publishing_house", length = 100, nullable = false)
    // @ApiModelProperty(notes = "Book publishing house", required = true, example = "Editorial Pepito")
    private String publishingHouse;

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

    @Column(name = "publish_date", nullable = false)
    // @ApiModelProperty(notes = "Book publish date", required = true, example = "2020-05-25")
    private LocalDate publishDate;

    @Column(name = "creation_date", nullable = false)
    // @ApiModelProperty(notes = "Book creation date", required = true, example = "2020-05-25")
    private LocalDateTime creationDate;

    @Column(name = "modification_date")
    // @ApiModelProperty(notes = "Book modification date", example = "2020-06-01")
    private LocalDateTime modificationDate;

    // BOOK(M):LIBRARY(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library_id")
    private Library library;

    // BOOK(M):CATEGORY(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @PrePersist
    public void prePersist() {
        status = "A";
        ISBN = Utils.makeISBN();
        creationDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        modificationDate = LocalDateTime.now();
    }
}
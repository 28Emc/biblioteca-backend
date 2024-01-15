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
    private Long id;

    @Column(name = "isbn", length = 20, unique = true, nullable = false)
    private String ISBN;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(length = 100, nullable = false)
    private String author;

    @Column(name = "publishing_house", length = 100, nullable = false)
    private String publishingHouse;

    @Column(columnDefinition = "text")
    private String synopsis;

    @Column(length = 4, nullable = false)
    private Integer stock;

    @Column(columnDefinition = "text", nullable = false)
    private String image;

    @Column(length = 1, nullable = false)
    private String status;

    @Column(name = "publish_date", nullable = false)
    private LocalDate publishDate;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "modification_date")
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
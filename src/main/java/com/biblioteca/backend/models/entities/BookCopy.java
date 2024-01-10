package com.biblioteca.backend.models.entities;

import com.biblioteca.backend.utils.Utils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_book_copy")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookCopy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @ApiModelProperty(notes = "Book copy ID")
    private Long id;

    @Column(name = "isbn", length = 20, nullable = false)
    // @ApiModelProperty(notes = "Book ISBN", required = true, example = "9791234567896")
    private String ISBN;

    @Column(nullable = false)
    // @ApiModelProperty(notes = "Book copy status", required = true, example = "A")
    private String status;

    @Column(name = "creation_date", nullable = false)
    // @ApiModelProperty(notes = "Book copy creation date", required = true, example = "2020-05-25")
    private LocalDateTime creationDate;

    @Column(name = "modification_date")
    // @ApiModelProperty(notes = "Book copy modification date", example = "2020-06-01")
    private LocalDateTime modificationDate;

    // BOOK_COPY(M):BOOK(1)
    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @PrePersist
    public void prePersist() {
        status = "D"; // DISPONIBLE
        creationDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        modificationDate = LocalDateTime.now();
    }
}
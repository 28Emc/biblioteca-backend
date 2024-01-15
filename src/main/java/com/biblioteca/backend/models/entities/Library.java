package com.biblioteca.backend.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Library {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "address", length = 100, unique = true, nullable = false)
    private String address;

    @Column(columnDefinition = "text")
    private String addressReference;

    @Column(columnDefinition = "text")
    private String imageReference;

    @Column(length = 1, nullable = false)
    private String status;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "modification_date")
    private LocalDateTime modificationDate;

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
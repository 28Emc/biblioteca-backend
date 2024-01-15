package com.biblioteca.backend.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, unique = true, nullable = false)
    private String name;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "modification_date")
    private LocalDateTime modificationDate;

    @Column(name = "status", length = 1, nullable = false)
    private String status;

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
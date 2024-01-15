package com.biblioteca.backend.models.entities;

import com.biblioteca.backend.utils.Utils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String uuid;

    @Column(length = 60, nullable = false)
    private String name;

    @Column(name = "last_name", length = 100, nullable = false)
    private String lastName;

    @Column(name = "position", length = 30, nullable = false)
    private String position;

    @Column(name = "doc_nro", length = 20, nullable = false, unique = true)
    private String docNro;

    @Column(name = "phone_number", length = 9, unique = true, nullable = false)
    private String phoneNumber;

    @Column(length = 30, unique = true, nullable = false)
    private String email;

    @Column(length = 1, nullable = false)
    private String status;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "modification_date")
    private LocalDateTime modificationDate;

    // EMPLOYEE(M):LIBRARY(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library_id")
    private Library library;

    @PrePersist
    public void prePersist() {
        uuid = Utils.makeRandom10StringCode("EM");
        status = "A";
        creationDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        modificationDate = LocalDateTime.now();
    }
}
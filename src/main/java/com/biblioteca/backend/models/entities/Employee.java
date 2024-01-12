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
    // @ApiModelProperty(notes = "Employee ID")
    private Long id;

    @Column(nullable = false)
    // @ApiModelProperty(notes = "Employee uuid", required = true, example = "EM12535435")
    private String uuid;

    @Column(length = 60, nullable = false)
    // @ApiModelProperty(notes = "Employee name", required = true, example = "Manolito")
    private String name;

    @Column(name = "last_name", length = 100, nullable = false)
    // @ApiModelProperty(notes = "Employee last name", required = true, example = "Paredes Rojas")
    private String lastName;

    @Column(name = "position", length = 30, nullable = false)
    // @ApiModelProperty(notes = "Employee position", example = "EMPLOYEE")
    private String position;

    @Column(name = "doc_nro", length = 20, nullable = false, unique = true)
    // @ApiModelProperty(notes = "Employee doc nro", example = "22244543")
    private String docNro;

    @Column(name = "phone_number", length = 9, unique = true, nullable = false)
    // @ApiModelProperty(notes = "Employee phone number", required = true, example = "967003658")
    private String phoneNumber;

    @Column(length = 30, unique = true, nullable = false)
    // @ApiModelProperty(notes = "Employee email", required = true, example = "mparedes@gmail.com")
    private String email;

    @Column(nullable = false)
    // @ApiModelProperty(notes = "Employee status", required = true, example = "A")
    private String status;

    @Column(name = "creation_date", nullable = false)
    // @ApiModelProperty(notes = "Employee creation date", required = true, example = "2020-05-25")
    private LocalDateTime creationDate;

    @Column(name = "modification_date")
    // @ApiModelProperty(notes = "Employee modification date", example = "2020-06-01")
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
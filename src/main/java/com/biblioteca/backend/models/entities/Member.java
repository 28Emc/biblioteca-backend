package com.biblioteca.backend.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String uuid;

    @Column(length = 60, nullable = false)
    private String name;

    @Column(name = "last_name", length = 100, nullable = false)
    private String lastName;

    @Column(name = "doc_type", length = 1, nullable = false)
    private String docType;

    @Column(name = "doc_nro", length = 20, nullable = false, unique = true)
    private String docNro;

    @Column(length = 200)
    private String address;

    @Column(name = "address_reference", columnDefinition = "text")
    private String addressReference;

    @Column(name = "phone_number", length = 9, unique = true, nullable = false)
    private String phoneNumber;

    @Column(length = 30, unique = true, nullable = false)
    private String email;

    @Column(length = 10, unique = true)
    private String alias;

    @Column(length = 1, nullable = false)
    private String status;

    @Column(name = "avatar_img")
    private String avatarImg;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "modification_date")
    private LocalDateTime modificationDate;

    @PrePersist
    public void prePersist() {
        uuid = UUID.randomUUID().toString();
        status = "A";
        creationDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        modificationDate = LocalDateTime.now();
    }
}
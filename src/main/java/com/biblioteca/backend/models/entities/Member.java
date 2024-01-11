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
    // @ApiModelProperty(notes = "Member ID")
    private Long id;

    @Column(nullable = false)
    // @ApiModelProperty(notes = "Member uuid", required = true, example = "24324341-142314-14")
    private String uuid;

    @Column(length = 60, nullable = false)
    // @ApiModelProperty(notes = "Member name", required = true, example = "Pepito Pancho")
    private String name;

    @Column(name = "last_name", length = 100, nullable = false)
    // @ApiModelProperty(notes = "Member last name", required = true, example = "Paredes Rojas")
    private String lastName;

    @Column(name = "doc_type", length = 1, nullable = false)
    // @ApiModelProperty(notes = "Member doc type", example = "DNI")
    private String docType;

    @Column(name = "doc_nro", length = 20, nullable = false, unique = true)
    // @ApiModelProperty(notes = "Member doc nro", example = "22244543")
    private String docNro;

    @Column(length = 200)
    // @ApiModelProperty(notes = "Member address", example = "Av. Lima 123")
    private String address;

    @Column(name = "address_reference", columnDefinition = "text")
    // @ApiModelProperty(notes = "Member address reference", example = "Cerca a iglesia municipal")
    private String addressReference;

    @Column(name = "phone_number", length = 9, unique = true, nullable = false)
    // @ApiModelProperty(notes = "Member phone number", required = true, example = "987123654")
    private String phoneNumber;

    @Column(length = 30, unique = true, nullable = false)
    // @ApiModelProperty(notes = "Member email", required = true, example = "pepe@gmail.com")
    private String email;

    @Column(length = 10, unique = true)
    // @ApiModelProperty(notes = "Member alias", example = "pepito2020")
    private String alias;

    @Column(nullable = false)
    // @ApiModelProperty(notes = "Member status", required = true, example = "A")
    private String status;

    @Column(name = "avatar_img")
    // @ApiModelProperty(notes = "Member avatar image", example = "https://www.example.com/pepito2020.png")
    private String avatarImg;

    @Column(name = "creation_date", nullable = false)
    // @ApiModelProperty(notes = "Member creation date", required = true, example = "2020-05-25")
    private LocalDateTime creationDate;

    @Column(name = "modification_date")
    // @ApiModelProperty(notes = "Member modification date", example = "2020-06-01")
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
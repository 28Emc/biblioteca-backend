package com.biblioteca.backend.models.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private Long id;

    private String uuid;

    @NotEmpty(message = "{notEmpty.memberDTO.name}")
    @Size(min = 4, max = 60, message = "{size.memberDTO.name}")
    private String name;

    @NotEmpty(message = "{notEmpty.memberDTO.lastName}")
    @Size(min = 1, max = 100, message = "{size.memberDTO.lastName}")
    private String lastName;

    @NotEmpty(message = "{notEmpty.memberDTO.docType}")
    @Size(max = 1, message = "{size.memberDTO.docType}")
    private String docType;

    @NotEmpty(message = "{notEmpty.memberDTO.docNro}")
    @Size(max = 20, message = "{size.memberDTO.docNro}")
    private String docNro;

    private String address;

    private String addressReference;

    @NotEmpty(message = "{notEmpty.memberDTO.phoneNumber}")
    @Size(min = 9, max = 9, message = "{size.memberDTO.phoneNumber}")
    private String phoneNumber;

    @NotEmpty(message = "{notEmpty.memberDTO.email}")
    @Size(min = 9, max = 30, message = "{size.memberDTO.email}")
    private String email;

    @Size(min = 5, max = 10, message = "{size.memberDTO.alias}")
    private String alias;

    private String avatarImg;

    private String status;

    private LocalDateTime creationDate;
}

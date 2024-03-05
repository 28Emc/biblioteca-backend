package com.emc.bibliotecamscore.models.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class EmployeeDTO {
    private Long id;

    @NotNull(message = "{notNull.employeeDTO.libraryId}")
    private Integer libraryId;

    private String uuid;

    @NotEmpty(message = "{notEmpty.employeeDTO.name}")
    @Size(min = 4, max = 60, message = "{size.employeeDTO.name}")
    private String name;

    @NotEmpty(message = "{notEmpty.employeeDTO.lastName}")
    @Size(min = 4, max = 100, message = "{size.employeeDTO.lastName}")
    private String lastName;

    @NotEmpty(message = "{notEmpty.employeeDTO.position}")
    @Size(max = 30, message = "{size.employeeDTO.position}")
    private String position;

    @NotEmpty(message = "{notEmpty.employeeDTO.docNro}")
    @Size(max = 20, message = "{size.employeeDTO.docNro}")
    private String docNro;

    @NotEmpty(message = "{notEmpty.employeeDTO.phoneNumber}")
    @Size(min = 9, max = 9, message = "{size.employeeDTO.phoneNumber}")
    private String phoneNumber;

    @NotEmpty(message = "{notEmpty.employeeDTO.email}")
    @Size(min = 9, max = 30, message = "{size.employeeDTO.email}")
    private String email;

    private String status;

    private LocalDateTime creationDate;
}

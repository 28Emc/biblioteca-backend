package com.emc.bibliotecamscore.models.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LibraryDTO {
    @NotEmpty(message = "{notEmpty.libraryDTO.address}")
    @Size(max = 100, message = "{size.libraryDTO.address}")
    private String address;

    @Size(max = 100, message = "{size.libraryDTO.addressReference}")
    private String addressReference;

    private String imageReference;
}

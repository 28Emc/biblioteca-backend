package com.biblioteca.backend.models.dtos;

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
public class BookCopyDTO {
    @NotEmpty(message = "{notEmpty.bookCopyDTO.isbn}")
    @Size(max = 20, message = "{size.bookCopyDTO.isbn}")
    private String ISBN;
}

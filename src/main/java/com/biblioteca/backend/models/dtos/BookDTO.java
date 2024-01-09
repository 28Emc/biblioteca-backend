package com.biblioteca.backend.models.dtos;

import com.biblioteca.backend.enums.CustomDateConstraint;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    @NotEmpty(message = "{notEmpty.bookDTO.isbn}")
    @Size(max = 20, message = "{size.bookDTO.isbn}")
    private String ISBN;

    @NotEmpty(message = "{notEmpty.bookDTO.title}")
    @Size(max = 100, message = "{size.bookDTO.title}")
    private String title;

    @NotEmpty(message = "{notEmpty.bookDTO.author}")
    @Size(max = 100, message = "{size.bookDTO.author}")
    private String author;

    @NotEmpty(message = "{notEmpty.bookDTO.synopsis}")
    @Size(max = 5000, message = "{size.bookDTO.synopsis}")
    private String synopsis;

    @NotNull(message = "{notNull.bookDTO.stock}")
    @Min(value = 1, message = "{min.bookDTO.stock}")
    @Max(value = 9999, message = "{max.bookDTO.stock}")
    private Integer stock;

    @NotEmpty(message = "{notEmpty.bookDTO.image}")
    private String image;

    @NotNull(message = "{notNull.bookDTO.pubblishDate}")
    @CustomDateConstraint(message = "{valid.bookDTO.pubblishDate}")
    private String pubblishDate;
}

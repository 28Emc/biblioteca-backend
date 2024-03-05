package com.emc.bibliotecamscore.models.dtos;

import com.emc.bibliotecamscore.enums.CustomDateConstraint;
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
    private String id;

    @NotNull(message = "{notNull.bookDTO.categoryId}")
    private Integer categoryId;

    @NotNull(message = "{notNull.bookDTO.libraryId}")
    private Integer libraryId;

    @NotEmpty(message = "{notEmpty.bookDTO.title}")
    @Size(max = 100, message = "{size.bookDTO.title}")
    private String title;

    @NotEmpty(message = "{notEmpty.bookDTO.author}")
    @Size(max = 100, message = "{size.bookDTO.author}")
    private String author;

    @NotEmpty(message = "{notEmpty.bookDTO.publishingHouse}")
    @Size(max = 100, message = "{size.bookDTO.publishingHouse}")
    private String publishingHouse;

    @NotEmpty(message = "{notEmpty.bookDTO.synopsis}")
    @Size(max = 5000, message = "{size.bookDTO.synopsis}")
    private String synopsis;

    @NotNull(message = "{notNull.bookDTO.stock}")
    @Min(value = 1, message = "{min.bookDTO.stock}")
    @Max(value = 9999, message = "{max.bookDTO.stock}")
    private Integer stock;

    @NotEmpty(message = "{notEmpty.bookDTO.image}")
    private String image;

    @NotNull(message = "{notNull.bookDTO.publishDate}")
    @CustomDateConstraint(message = "{valid.bookDTO.publishDate}")
    private String publishDate;
}

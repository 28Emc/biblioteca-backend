package com.emc.bibliotecamscore.models.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookCopyDTO {
    @NotNull(message = "{notNull.bookCopyDTO.libraryId}")
    private Long libraryId;

    @NotEmpty(message = "{notEmpty.bookCopyDTO.isbn}")
    @Size(max = 20, message = "{size.bookCopyDTO.isbn}")
    private String ISBN;

    @NotNull(message = "{notNull.bookCopyDTO.quantity}")
    @Min(value = 1, message = "{min.bookCopyDTO.quantity}")
    @Max(value = 100000, message = "{max.bookCopyDTO.quantity}")
    private Integer quantity;
}

package com.biblioteca.backend.models.dtos;

import com.biblioteca.backend.enums.EnumNamePattern;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryStatusDTO {
    @NotEmpty(message = "{notEmpty.categoryStatusDTO.status}")
    // @EnumNamePattern(regexp = "A|I", message = "{notValid.categoryStatusDTO.status}")
    @Pattern(regexp = "[AI]", message = "{pattern.categoryStatusDTO.status}")
    private String status;
}

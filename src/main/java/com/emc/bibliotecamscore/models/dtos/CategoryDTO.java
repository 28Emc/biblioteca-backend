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
public class CategoryDTO {
    @NotEmpty(message = "{notEmpty.categoryDTO.name}")
    @Size(min = 4, max = 30, message = "{size.categoryDTO.name}")
    private String name;
}

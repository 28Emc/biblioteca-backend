package com.biblioteca.backend.models.dtos;

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
public class UpdateStatusDTO {
    @NotEmpty(message = "{notEmpty.updateStatusDTO.status}")
    // @EnumNamePattern(regexp = "A|I", message = "{notValid.updateStatusDTO.status}")
    @Pattern(regexp = "[AI]", message = "{pattern.updateStatusDTO.status}")
    private String status;
}

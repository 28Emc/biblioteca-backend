package com.emc.bibliotecamscore.models.dtos;

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
    // @EnumNamePattern(regexp = "A|I|D|P|O|C|R", message = "{notValid.updateStatusDTO.status}")
    @Pattern(regexp = "[AIDPOCR]", message = "{pattern.updateStatusDTO.status}")
    private String status;
}

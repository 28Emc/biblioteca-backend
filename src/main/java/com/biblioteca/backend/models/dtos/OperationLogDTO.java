package com.biblioteca.backend.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogDTO {
    private String operationType;

    private String entityName;

    private Long entityId;

    private Long userId;
}

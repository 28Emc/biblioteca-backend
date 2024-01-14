package com.biblioteca.backend.services;

import com.biblioteca.backend.models.dtos.OperationLogDTO;

public interface IOperationLogService {
    void create(OperationLogDTO operationLogDTO);
}

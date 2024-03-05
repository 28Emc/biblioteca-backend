package com.emc.bibliotecamscore.services;

import com.emc.bibliotecamscore.models.dtos.OperationLogDTO;

public interface IOperationLogService {
    void create(OperationLogDTO operationLogDTO);
}

package com.biblioteca.backend.services;

import com.biblioteca.backend.models.dtos.OperationLogDTO;
import com.biblioteca.backend.models.entities.OperationLog;
import com.biblioteca.backend.repositories.IOperationLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OperationLogServiceImpl implements IOperationLogService {

    private final IOperationLogRepository operationLogRepository;

    public OperationLogServiceImpl(IOperationLogRepository operationLogRepository) {
        this.operationLogRepository = operationLogRepository;
    }

    @Override
    @Transactional
    public void create(OperationLogDTO operationLogDTO) {
        OperationLog operationLog = new OperationLog();
        operationLog.setOperationType(operationLogDTO.getOperationType());
        operationLog.setEntityName(operationLogDTO.getEntityName());
        operationLog.setEntityId(operationLog.getEntityId());
        operationLog.setUserId(operationLogDTO.getEntityId());
        operationLogRepository.save(operationLog);
    }
}

package com.emc.bibliotecamscore.repositories;

import com.emc.bibliotecamscore.models.entities.OperationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IOperationLogRepository extends JpaRepository<OperationLog, Long> {
}
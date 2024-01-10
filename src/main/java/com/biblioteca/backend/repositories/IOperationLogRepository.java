package com.biblioteca.backend.repositories;

import com.biblioteca.backend.models.entities.OperationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IOperationLogRepository extends JpaRepository<OperationLog, Long> {
}
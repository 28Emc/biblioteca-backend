package com.biblioteca.backend.services;

import com.biblioteca.backend.models.dtos.EmployeeDTO;
import com.biblioteca.backend.models.dtos.UpdateStatusDTO;
import com.biblioteca.backend.models.entities.Employee;
import com.biblioteca.backend.models.projections.EmployeeView;

import java.util.List;
import java.util.Optional;

public interface IEmployeeService {
    List<Employee> findAll();

    List<EmployeeView> findAllWithView();

    List<Employee> findByLibraryId(Long libraryId);

    List<EmployeeView> findByLibraryIdWithView(Long libraryId);

    Optional<Employee> findById(Long id);

    Optional<EmployeeView> findByIdWithView(Long id);

    Optional<Employee> findByUuid(String uuid);

    Optional<EmployeeView> findByUuidWithView(String uuid);

    Optional<Employee> findByDocNro(String docNro);

    Optional<EmployeeView> findByDocNroWithView(String docNro);

    void save(EmployeeDTO employeeDTO);

    void updateStatus(Long id, UpdateStatusDTO updateStatusDTO);
}

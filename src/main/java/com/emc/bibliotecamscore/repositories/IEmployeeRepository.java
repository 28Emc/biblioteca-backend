package com.emc.bibliotecamscore.repositories;

import com.emc.bibliotecamscore.models.entities.Employee;
import com.emc.bibliotecamscore.models.projections.EmployeeView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IEmployeeRepository extends JpaRepository<Employee, Long> {
    // INTERFACE BASED PROJECTION (OPENED)
    @Query(value = "SELECT e.id as employee_id, e.*, l.address as library_address, l.* FROM tb_employee e " +
            "INNER JOIN tb_library l ON l.id = e.library_id", nativeQuery = true)
    List<EmployeeView> findAllWithView();

    @Query(value = "SELECT e.id as employee_id, e.*, l.address as library_address, l.* FROM tb_employee e " +
            "INNER JOIN tb_library l ON l.id = e.library_id WHERE e.id = :id", nativeQuery = true)
    Optional<EmployeeView> findByIdWithView(Long id);

    List<Employee> findByLibraryId(Long libraryId);

    // INTERFACE BASED PROJECTION (CLOSED)
    @Query(value = "SELECT e.id as employee_id, e.*, l.address as library_address, l.* FROM tb_employee e " +
            "INNER JOIN tb_library l ON l.id = e.library_id WHERE l.id = :libraryId", nativeQuery = true)
    List<EmployeeView> findByLibraryIdWithView(Long libraryId);

    Optional<Employee> findByUuid(String uuid);

    @Query(value = "SELECT e.id as employee_id, e.*, l.address as library_address, l.* FROM tb_employee e " +
            "INNER JOIN tb_library l ON l.id = e.library_id WHERE e.uuid = :uuid", nativeQuery = true)
    Optional<EmployeeView> findByUuidWithView(String uuid);

    Optional<Employee> findByDocNro(String docNro);

    @Query(value = "SELECT e.id as employee_id, e.*, l.address as library_address, l.* FROM tb_employee e " +
            "INNER JOIN tb_library l ON l.id = e.library_id WHERE e.doc_nro = :docNro", nativeQuery = true)
    Optional<EmployeeView> findByDocNroWithView(String docNro);
}
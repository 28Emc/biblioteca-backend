package com.emc.bibliotecamscore.services;

import com.emc.bibliotecamscore.models.dtos.EmployeeDTO;
import com.emc.bibliotecamscore.models.dtos.UpdateStatusDTO;
import com.emc.bibliotecamscore.models.entities.Employee;
import com.emc.bibliotecamscore.models.entities.Library;
import com.emc.bibliotecamscore.models.projections.EmployeeView;
import com.emc.bibliotecamscore.repositories.IEmployeeRepository;
import com.emc.bibliotecamscore.repositories.ILibraryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements IEmployeeService {

    private final IEmployeeRepository employeeRepository;

    private final ILibraryRepository libraryRepository;

    public EmployeeServiceImpl(IEmployeeRepository employeeRepository,
                               ILibraryRepository libraryRepository) {
        this.employeeRepository = employeeRepository;
        this.libraryRepository = libraryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeView> findAllWithView() {
        return employeeRepository.findAllWithView();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> findByLibraryId(Long libraryId) {
        return employeeRepository.findByLibraryId(libraryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeView> findByLibraryIdWithView(Long libraryId) {
        return employeeRepository.findByLibraryIdWithView(libraryId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Employee> findById(Long id) {
        return employeeRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmployeeView> findByIdWithView(Long id) {
        return employeeRepository.findByIdWithView(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Employee> findByUuid(String uuid) {
        return employeeRepository.findByUuid(uuid);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmployeeView> findByUuidWithView(String uuid) {
        return employeeRepository.findByUuidWithView(uuid);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Employee> findByDocNro(String docNro) {
        return employeeRepository.findByDocNro(docNro);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmployeeView> findByDocNroWithView(String docNro) {
        return employeeRepository.findByDocNroWithView(docNro);
    }

    @Override
    @Transactional
    public void save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        if (employeeDTO.getId() != null) {
            employee = findById(employeeDTO.getId())
                    .orElseThrow(() -> new NoSuchElementException("Employee not found"));
        }
        Library libraryFound = libraryRepository
                .findById(employeeDTO.getLibraryId().longValue())
                .orElseThrow(() -> new NoSuchElementException("Library not found"));
        employee.setLibrary(libraryFound);
        employee.setName(employeeDTO.getName());
        employee.setLastName(employeeDTO.getLastName());
        employee.setPosition(employeeDTO.getPosition());
        employee.setDocNro(employeeDTO.getDocNro());
        employee.setPhoneNumber(employeeDTO.getPhoneNumber());
        employee.setEmail(employeeDTO.getEmail());
        employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, UpdateStatusDTO updateStatusDTO) {
        Employee employee = findById(id).orElseThrow(() -> new NoSuchElementException("Employee not found"));
        employee.setStatus(updateStatusDTO.getStatus());
        employeeRepository.save(employee);
    }
}

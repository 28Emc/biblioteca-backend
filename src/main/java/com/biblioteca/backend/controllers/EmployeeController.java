package com.biblioteca.backend.controllers;

import com.biblioteca.backend.models.dtos.EmployeeDTO;
import com.biblioteca.backend.models.dtos.UpdateStatusDTO;
import com.biblioteca.backend.models.entities.Employee;
import com.biblioteca.backend.models.projections.EmployeeView;
import com.biblioteca.backend.services.IEmployeeService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = {"*", "http://localhost:4200"})
@RestController
// @Api(value = "employee", description = "Employee controller endpoints")
public class EmployeeController {
    private final IEmployeeService employeeService;

    public EmployeeController(IEmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /*
    @ApiOperation(value = "Método de listado de categorias", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Categorias encontrados"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar las categorias. " +
                    "Inténtelo mas tarde")})
     */
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/employees", produces = "application/json")
    public ResponseEntity<?> fetchAll() {
        Map<String, Object> response = new HashMap<>();
        List<Employee> employees;
        try {
            employees = employeeService.findAll();
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving employees");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("data", employees);
        response.put("message", "Data found");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
    @ApiOperation(value = "Método de listado de categorias", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Categorias encontrados"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar las categorias. " +
                    "Inténtelo mas tarde")})
     */
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/employees/view", produces = "application/json")
    public ResponseEntity<?> fetchAllWithView() {
        Map<String, Object> response = new HashMap<>();
        List<EmployeeView> employees;
        try {
            employees = employeeService.findAllWithView();
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving employees");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("data", employees);
        response.put("message", "Data found");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
    @ApiOperation(value = "Método de consulta de categoría por su id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Categoría encontrada"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar la categoría. " +
                    "Inténtelo mas tarde")})
    */
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/employees/{id}", produces = "application/json")
    public ResponseEntity<?> getOne(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        Employee employee;
        try {
            if (!id.matches("^\\d+$")) {
                response.put("message", "Invalid Employee ID");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            employee = employeeService.findById(Long.parseLong(id)).orElseThrow();
        } catch (NoSuchElementException e) {
            response.put("message", "Employee no found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the employee");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("data", employee);
        response.put("message", "Data found");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
    @ApiOperation(value = "Método de consulta de categoría por su id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Categoría encontrada"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar la categoría. " +
                    "Inténtelo mas tarde")})
    */
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/employees/{id}/view", produces = "application/json")
    public ResponseEntity<?> getOneWithView(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        EmployeeView employee;
        try {
            if (!id.matches("^\\d+$")) {
                response.put("message", "Invalid Employee ID");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            employee = employeeService.findByIdWithView(Long.parseLong(id)).orElseThrow();
        } catch (NoSuchElementException e) {
            response.put("message", "Employee no found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the employee");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("data", employee);
        response.put("message", "Data found");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
    @ApiOperation(value = "Método de consulta de categoría por su id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Categoría encontrada"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar la categoría. " +
                    "Inténtelo mas tarde")})
    */
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/employees/{libraryId}/library", produces = "application/json")
    public ResponseEntity<?> fetchByLibraryId(@PathVariable Long libraryId) {
        Map<String, Object> response = new HashMap<>();
        List<Employee> employee;
        try {
            employee = employeeService.findByLibraryId(libraryId);
        } catch (NoSuchElementException e) {
            response.put("message", "Employee not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the employee by library ID");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("data", employee);
        response.put("message", "Data found");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
    @ApiOperation(value = "Método de consulta de categoría por su id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Categoría encontrada"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar la categoría. " +
                    "Inténtelo mas tarde")})
    */
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/employees/{libraryId}/library/view", produces = "application/json")
    public ResponseEntity<?> fetchByLibraryIdWithView(@PathVariable Long libraryId) {
        Map<String, Object> response = new HashMap<>();
        List<EmployeeView> employee;
        try {
            employee = employeeService.findByLibraryIdWithView(libraryId);
        } catch (NoSuchElementException e) {
            response.put("message", "Employee not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the employee by library ID");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("data", employee);
        response.put("message", "Data found");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
    @ApiOperation(value = "Método de consulta de categoría por su id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Categoría encontrada"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar la categoría. " +
                    "Inténtelo mas tarde")})
    */
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/employees/{uuid}/uuid", produces = "application/json")
    public ResponseEntity<?> getOneByUuid(@PathVariable String uuid) {
        Map<String, Object> response = new HashMap<>();
        Employee employee;
        try {
            employee = employeeService.findByUuid(uuid).orElseThrow();
        } catch (NoSuchElementException e) {
            response.put("message", "Employee not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the employee by uuid");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("data", employee);
        response.put("message", "Data found");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
    @ApiOperation(value = "Método de consulta de categoría por su id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Categoría encontrada"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar la categoría. " +
                    "Inténtelo mas tarde")})
    */
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/employees/{uuid}/uuid/view", produces = "application/json")
    public ResponseEntity<?> getOneByUuidWithView(@PathVariable String uuid) {
        Map<String, Object> response = new HashMap<>();
        EmployeeView employee;
        try {
            employee = employeeService.findByUuidWithView(uuid).orElseThrow();
        } catch (NoSuchElementException e) {
            response.put("message", "Employee not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the employee by uuid");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("data", employee);
        response.put("message", "Data found");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
    @ApiOperation(value = "Método de registro de categorias", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Categoría registrada"),
            @ApiResponse(code = 400, message = "La categoría ya existe"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = "El nombre de la " +
            "employee es requerido"),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de registrar la categoría. " +
                    "Inténtelo mas tarde")})
    */
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")

    @PostMapping(value = "/employees", produces = "application/json")
    public ResponseEntity<?> create(@Valid @RequestBody EmployeeDTO employeeDTO,
                                    BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        Optional<Employee> employeeFound;
        try {
            if (result.hasErrors()) {
                List<String> errors = new ArrayList<>();
                for (FieldError fieldError : result.getFieldErrors()) {
                    errors.add(fieldError.getDefaultMessage());
                }
                response.put("message", errors);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            employeeFound = employeeService.findByDocNro(employeeDTO.getDocNro());
            if (employeeFound.isPresent()) {
                response.put("message", "Employee already exists");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            employeeDTO.setId(null);
            employeeService.save(employeeDTO);
        } catch (NoSuchElementException e) {
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("message", "There was an error while registering the employee");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "The employee was registered successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /*
    @ApiOperation(value = "Método de actualización de categorias", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Categoría actualizada"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "La categoría no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de actualizar la categoría. " +
                    "Inténtelo mas tarde")})
    */
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")

    @PutMapping(value = "/employees/{id}", produces = "application/json")
    public ResponseEntity<?> update(@Valid @RequestBody EmployeeDTO employeeDTO, BindingResult result,
                                    @PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!id.matches("^\\d+$")) {
                response.put("message", "Invalid employee ID");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            if (result.hasErrors()) {
                List<String> errors = new ArrayList<>();
                for (FieldError fieldError : result.getFieldErrors()) {
                    errors.add(fieldError.getDefaultMessage());
                }
                response.put("message", errors);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            /*
            else if (employeeService.findByTitle(bookDTO.getTitle()).isPresent() &&
                    !bookFound.getTitle().equals(bookDTO.getTitle())) {
                response.put("message", "Employee already exists");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            */
            employeeDTO.setId(Long.parseLong(id));
            employeeService.save(employeeDTO);
        } catch (NoSuchElementException e) {
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("message", "There was an error while updating employee values");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "The employee was updated successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
    @ApiOperation(value = "Método de deshabilitación de la categoría mediante el id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Categoría deshabilitada"),
            @ApiResponse(code = 201, message = " "), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "La categoría no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de deshabilitar la categoría. " +
                    "Inténtelo mas tarde")})
    */
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")

    @PutMapping(value = "/employees/{id}/status", produces = "application/json")
    public ResponseEntity<?> updateStatus(@Valid @RequestBody UpdateStatusDTO updateStatusDTO,
                                          BindingResult result, @PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!id.matches("^\\d+$")) {
                response.put("message", "Invalid Employee ID");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            if (result.hasErrors()) {
                List<String> errors = new ArrayList<>();
                for (FieldError fieldError : result.getFieldErrors()) {
                    errors.add(fieldError.getDefaultMessage());
                }
                response.put("message", errors);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            employeeService.updateStatus(Long.parseLong(id), updateStatusDTO);
        } catch (NoSuchElementException e) {
            response.put("message", "Employee or library not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("message", "There was an error while updating employee status");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "The employee status was updated successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

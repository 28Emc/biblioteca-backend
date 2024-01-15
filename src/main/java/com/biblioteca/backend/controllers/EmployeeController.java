package com.biblioteca.backend.controllers;

import com.biblioteca.backend.models.dtos.EmployeeDTO;
import com.biblioteca.backend.models.dtos.UpdateStatusDTO;
import com.biblioteca.backend.models.entities.Employee;
import com.biblioteca.backend.models.projections.EmployeeView;
import com.biblioteca.backend.services.IEmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.biblioteca.backend.utils.Utils.ID_REGEXP;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@CrossOrigin(origins = {"*", "http://localhost:4200"})
@RestController
@Tag(name = "Employee", description = "Employee operations")
public class EmployeeController {
    private final IEmployeeService employeeService;

    public EmployeeController(IEmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Operation(summary = "Fetch employee list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityListSchema"))
            }),
            // @ApiResponse(responseCode = 401, description = ""),
            // @ApiResponse(responseCode = "403", description = ""),
            // @ApiResponse(responseCode = "404", description = ""),
            @ApiResponse(responseCode = "500", description = "There was an error while retrieving the employees",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/employees", produces = "application/json")
    public ResponseEntity<?> fetchAll() {
        Map<String, Object> response = new HashMap<>();
        List<Employee> employees;
        try {
            employees = employeeService.findAll();
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving employees");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "Data found");
        response.put("details", employees);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Fetch employee list with custom view")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityListSchema"))
            }),
            // @ApiResponse(responseCode = 401, description = ""),
            // @ApiResponse(responseCode = "403", description = ""),
            // @ApiResponse(responseCode = "404", description = ""),
            @ApiResponse(responseCode = "500", description = "There was an error while retrieving the employees",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/employees/view", produces = "application/json")
    public ResponseEntity<?> fetchAllWithView() {
        Map<String, Object> response = new HashMap<>();
        List<EmployeeView> employees;
        try {
            employees = employeeService.findAllWithView();
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving employees");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "Data found");
        response.put("details", employees);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get employee by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid employee ID", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            // @ApiResponse(responseCode = "401", description = "", content = @Content),
            // @ApiResponse(responseCode = "403", description = "", content = @Content),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            @ApiResponse(responseCode = "500", description = "There was an error while retrieving the employee",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/employees/{id}", produces = "application/json")
    public ResponseEntity<?> getOne(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        Employee employee;
        try {
            if (!id.matches(ID_REGEXP)) {
                response.put("message", "Invalid Employee ID");
                response.put("details", List.of());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            employee = employeeService.findById(Long.parseLong(id)).orElseThrow();
        } catch (NoSuchElementException e) {
            response.put("message", "Employee not found");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the employee");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "Data found");
        response.put("details", employee);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get employee by ID with custom view")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid employee ID", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            // @ApiResponse(responseCode = "401", description = "", content = @Content),
            // @ApiResponse(responseCode = "403", description = "", content = @Content),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            @ApiResponse(responseCode = "500", description = "There was an error while retrieving the employee",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/employees/{id}/view", produces = "application/json")
    public ResponseEntity<?> getOneWithView(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        EmployeeView employee;
        try {
            if (!id.matches(ID_REGEXP)) {
                response.put("message", "Invalid Employee ID");
                response.put("details", List.of());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            employee = employeeService.findByIdWithView(Long.parseLong(id)).orElseThrow();
        } catch (NoSuchElementException e) {
            response.put("message", "Employee not found");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the employee");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "Data found");
        response.put("details", employee);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get employee by library ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid library ID", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            // @ApiResponse(responseCode = "401", description = "", content = @Content),
            // @ApiResponse(responseCode = "403", description = "", content = @Content),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            @ApiResponse(responseCode = "500", description = "There was an error while retrieving the employee",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/employees/{libraryId}/library", produces = "application/json")
    public ResponseEntity<?> fetchByLibraryId(@PathVariable String libraryId) {
        Map<String, Object> response = new HashMap<>();
        List<Employee> employee;
        try {
            if (!libraryId.matches(ID_REGEXP)) {
                response.put("message", "Invalid library ID");
                response.put("details", List.of());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            employee = employeeService.findByLibraryId(Long.parseLong(libraryId));
        } catch (NoSuchElementException e) {
            response.put("message", "Employee not found");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the employee by library ID");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "Data found");
        response.put("details", employee);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get employee by library ID with custom view")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid library ID", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            // @ApiResponse(responseCode = "401", description = "", content = @Content),
            // @ApiResponse(responseCode = "403", description = "", content = @Content),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            @ApiResponse(responseCode = "500", description = "There was an error while retrieving the employee",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/employees/{libraryId}/library/view", produces = "application/json")
    public ResponseEntity<?> fetchByLibraryIdWithView(@PathVariable String libraryId) {
        Map<String, Object> response = new HashMap<>();
        List<EmployeeView> employee;
        try {
            if (!libraryId.matches(ID_REGEXP)) {
                response.put("message", "Invalid library ID");
                response.put("details", List.of());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            employee = employeeService.findByLibraryIdWithView(Long.parseLong(libraryId));
        } catch (NoSuchElementException e) {
            response.put("message", "Employee not found");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the employee by library ID");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "Data found");
        response.put("details", employee);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get employee by UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            // @ApiResponse(responseCode = "401", description = "", content = @Content),
            // @ApiResponse(responseCode = "403", description = "", content = @Content),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            @ApiResponse(responseCode = "500", description = "There was an error while retrieving the employee",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/employees/{uuid}/uuid", produces = "application/json")
    public ResponseEntity<?> getOneByUuid(@PathVariable String uuid) {
        Map<String, Object> response = new HashMap<>();
        Employee employee;
        try {
            employee = employeeService.findByUuid(uuid).orElseThrow();
        } catch (NoSuchElementException e) {
            response.put("message", "Employee not found");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the employee by uuid");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "Data found");
        response.put("details", employee);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get employee by UUID with custom view")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            // @ApiResponse(responseCode = "401", description = "", content = @Content),
            // @ApiResponse(responseCode = "403", description = "", content = @Content),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            @ApiResponse(responseCode = "500", description = "There was an error while retrieving the employee",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/employees/{uuid}/uuid/view", produces = "application/json")
    public ResponseEntity<?> getOneByUuidWithView(@PathVariable String uuid) {
        Map<String, Object> response = new HashMap<>();
        EmployeeView employee;
        try {
            employee = employeeService.findByUuidWithView(uuid).orElseThrow();
        } catch (NoSuchElementException e) {
            response.put("message", "Employee not found");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the employee by uuid");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "Data found");
        response.put("details", employee);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Register a employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employee registered successfully", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            @ApiResponse(responseCode = "400", description = "Employee already exists / " +
                    "There was an error while registering the employee",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    }),
            // @ApiResponse(responseCode = "401", description = "", content = @Content),
            // @ApiResponse(responseCode = "403", description = "", content = @Content),
            @ApiResponse(responseCode = "404", description = "Library not found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            @ApiResponse(responseCode = "500", description = "There was an error while registering the employee",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
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
                response.put("message", "There was an error while registering the employee");
                response.put("details", errors);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            employeeFound = employeeService.findByDocNro(employeeDTO.getDocNro());
            if (employeeFound.isPresent()) {
                response.put("message", "Employee already exists");
                response.put("details", List.of());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            employeeDTO.setId(null);
            employeeService.save(employeeDTO);
        } catch (NoSuchElementException e) {
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("message", "There was an error while registering the employee");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "The employee was registered successfully");
        response.put("details", null);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Modify employee values")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee updated successfully", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            @ApiResponse(responseCode = "400", description = "Employee already exists / " +
                    "There was an error while updating the employee",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    }),
            // @ApiResponse(responseCode = "401", description = "", content = @Content),
            // @ApiResponse(responseCode = "403", description = "", content = @Content),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            @ApiResponse(responseCode = "500", description = "There was an error while updating the employee",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    @PutMapping(value = "/employees/{id}", produces = "application/json")
    public ResponseEntity<?> update(@Valid @RequestBody EmployeeDTO employeeDTO, BindingResult result,
                                    @PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!id.matches(ID_REGEXP)) {
                response.put("message", "Invalid employee ID");
                response.put("details", List.of());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            if (result.hasErrors()) {
                List<String> errors = new ArrayList<>();
                for (FieldError fieldError : result.getFieldErrors()) {
                    errors.add(fieldError.getDefaultMessage());
                }
                response.put("message", "There was an error while updating employee values");
                response.put("details", errors);
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
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("message", "There was an error while updating employee values");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "The employee was updated successfully");
        response.put("details", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Modify employee status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee status updated successfully", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid employee ID / " +
                    "There was an error while updating the employee status",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    }),
            // @ApiResponse(responseCode = "401", description = "", content = @Content),
            // @ApiResponse(responseCode = "403", description = "", content = @Content),
            @ApiResponse(responseCode = "404", description = "Employee not found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            @ApiResponse(responseCode = "500", description = "There was an error while updating the employee status",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    @PutMapping(value = "/employees/{id}/status", produces = "application/json")
    public ResponseEntity<?> updateStatus(@Valid @RequestBody UpdateStatusDTO updateStatusDTO,
                                          BindingResult result, @PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!id.matches(ID_REGEXP)) {
                response.put("message", "Invalid Employee ID");
                response.put("details", List.of());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            if (result.hasErrors()) {
                List<String> errors = new ArrayList<>();
                for (FieldError fieldError : result.getFieldErrors()) {
                    errors.add(fieldError.getDefaultMessage());
                }
                response.put("message", "There was an error while updating employee status");
                response.put("details", errors);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            employeeService.updateStatus(Long.parseLong(id), updateStatusDTO);
        } catch (NoSuchElementException e) {
            response.put("message", "Employee or library not found");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("message", "There was an error while updating employee status");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "The employee status was updated successfully");
        response.put("details", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

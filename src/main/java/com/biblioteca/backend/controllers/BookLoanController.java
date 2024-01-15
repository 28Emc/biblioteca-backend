package com.biblioteca.backend.controllers;

import com.biblioteca.backend.models.dtos.BookLoanDTO;
import com.biblioteca.backend.models.dtos.UpdateStatusDTO;
import com.biblioteca.backend.models.entities.BookLoan;
import com.biblioteca.backend.models.projections.BookLoanView;
import com.biblioteca.backend.services.IBookLoanService;
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
@Tag(name = "BookLoan", description = "Book loan operations")
public class BookLoanController {
    private final IBookLoanService bookLoanService;

    public BookLoanController(IBookLoanService bookLoanService) {
        this.bookLoanService = bookLoanService;
    }


    @Operation(summary = "Fetch book loan list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityListSchema"))
            }),
            // @ApiResponse(responseCode = 401, description = ""),
            // @ApiResponse(responseCode = "403", description = ""),
            // @ApiResponse(responseCode = "404", description = ""),
            @ApiResponse(responseCode = "500", description = "There was an error while retrieving the book loans",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/book-loans", produces = "application/json")
    public ResponseEntity<?> fetchAll() {
        Map<String, Object> response = new HashMap<>();
        List<BookLoan> bookLoans;
        try {
            bookLoans = bookLoanService.findAll();
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving book loans");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "Data found");
        response.put("details", bookLoans);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Fetch book loan list with custom view")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityListSchema"))
            }),
            // @ApiResponse(responseCode = 401, description = ""),
            // @ApiResponse(responseCode = "403", description = ""),
            // @ApiResponse(responseCode = "404", description = ""),
            @ApiResponse(responseCode = "500", description = "There was an error while retrieving the book loans",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/book-loans/view", produces = "application/json")
    public ResponseEntity<?> fetchAllWithView() {
        Map<String, Object> response = new HashMap<>();
        List<BookLoanView> bookLoans;
        try {
            bookLoans = bookLoanService.findAllWithView();
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving book loans");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "Data found");
        response.put("details", bookLoans);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get book loan by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid book loan ID", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            // @ApiResponse(responseCode = "401", description = "", content = @Content),
            // @ApiResponse(responseCode = "403", description = "", content = @Content),
            @ApiResponse(responseCode = "404", description = "Book loan not found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            @ApiResponse(responseCode = "500", description = "There was an error while retrieving the book loan",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/book-loans/{id}", produces = "application/json")
    public ResponseEntity<?> getOne(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        BookLoan bookLoan;
        try {
            if (!id.matches(ID_REGEXP)) {
                response.put("message", "Invalid book loan ID");
                response.put("details", List.of());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            bookLoan = bookLoanService.findById(Long.parseLong(id)).orElseThrow();
        } catch (NoSuchElementException e) {
            response.put("message", "Book loan not found");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the book loan by id");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "Data found");
        response.put("details", bookLoan);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get book loan by ID with custom view")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid book loan ID", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            // @ApiResponse(responseCode = "401", description = "", content = @Content),
            // @ApiResponse(responseCode = "403", description = "", content = @Content),
            @ApiResponse(responseCode = "404", description = "Book loan not found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            @ApiResponse(responseCode = "500", description = "There was an error while retrieving the book loan",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/book-loans/{id}/view", produces = "application/json")
    public ResponseEntity<?> getOneWithView(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        BookLoanView bookLoan;
        try {
            if (!id.matches(ID_REGEXP)) {
                response.put("message", "Invalid book loan ID");
                response.put("details", List.of());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            bookLoan = bookLoanService.findByIdWithView(Long.parseLong(id)).orElseThrow();
        } catch (NoSuchElementException e) {
            response.put("message", "Book loan not found");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the book loan by id");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "Data found");
        response.put("details", bookLoan);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get book loan by code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            // @ApiResponse(responseCode = "401", description = "", content = @Content),
            // @ApiResponse(responseCode = "403", description = "", content = @Content),
            @ApiResponse(responseCode = "404", description = "Book loan not found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            @ApiResponse(responseCode = "500", description = "There was an error while retrieving the book loan",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/book-loans/{code}/code", produces = "application/json")
    public ResponseEntity<?> getOneByCode(@PathVariable String code) {
        Map<String, Object> response = new HashMap<>();
        BookLoan bookLoan;
        try {
            bookLoan = bookLoanService.findByCode(code).orElseThrow();
        } catch (NoSuchElementException e) {
            response.put("message", "Book loan not found");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the book loan by code");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "Data found");
        response.put("details", bookLoan);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get book loan by code with custom view")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            // @ApiResponse(responseCode = "401", description = "", content = @Content),
            // @ApiResponse(responseCode = "403", description = "", content = @Content),
            @ApiResponse(responseCode = "404", description = "Book loan not found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            @ApiResponse(responseCode = "500", description = "There was an error while retrieving the book loan",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/book-loans/{code}/code/view", produces = "application/json")
    public ResponseEntity<?> getOneByCodeWithView(@PathVariable String code) {
        Map<String, Object> response = new HashMap<>();
        BookLoanView bookLoan;
        try {
            bookLoan = bookLoanService.findByCodeWithView(code).orElseThrow();
        } catch (NoSuchElementException e) {
            response.put("message", "Book loan not found");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the book loan by code");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "Data found");
        response.put("details", bookLoan);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Fetch book loans by status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            // @ApiResponse(responseCode = "401", description = "", content = @Content),
            // @ApiResponse(responseCode = "403", description = "", content = @Content),
            @ApiResponse(responseCode = "404", description = "Book loans not found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            @ApiResponse(responseCode = "500", description = "There was an error while retrieving the book loans",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/book-loans/{status}/status", produces = "application/json")
    public ResponseEntity<?> fetchByStatus(@PathVariable String status) {
        Map<String, Object> response = new HashMap<>();
        List<BookLoan> bookLoans;
        try {
            bookLoans = bookLoanService.findByStatus(status);
        } catch (NoSuchElementException e) {
            response.put("message", "Book loan not found");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the book loans by status");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "Data found");
        response.put("details", bookLoans);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Fetch book loans by status with custom view")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            // @ApiResponse(responseCode = "401", description = "", content = @Content),
            // @ApiResponse(responseCode = "403", description = "", content = @Content),
            @ApiResponse(responseCode = "404", description = "Book loans not found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            @ApiResponse(responseCode = "500", description = "There was an error while retrieving the book loans",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/book-loans/{status}/status/view", produces = "application/json")
    public ResponseEntity<?> findByStatusWithView(@PathVariable String status) {
        Map<String, Object> response = new HashMap<>();
        List<BookLoanView> bookLoans;
        try {
            bookLoans = bookLoanService.findByStatusWithView(status);
        } catch (NoSuchElementException e) {
            response.put("message", "Book loan not found");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the book loans by status");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "Data found");
        response.put("details", bookLoans);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Register a book loan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book loan registered successfully", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            @ApiResponse(responseCode = "400", description = "There was an error while registering the book loan",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    }),
            // @ApiResponse(responseCode = "401", description = "", content = @Content),
            // @ApiResponse(responseCode = "403", description = "", content = @Content),
            @ApiResponse(responseCode = "404", description = "Book, Employee or Member not found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            @ApiResponse(responseCode = "500", description = "There was an error while registering the book loan",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    @PostMapping(value = "/book-loans", produces = "application/json")
    public ResponseEntity<?> create(@Valid @RequestBody BookLoanDTO bookLoanDTO, BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (result.hasErrors()) {
                List<String> errors = new ArrayList<>();
                for (FieldError fieldError : result.getFieldErrors()) {
                    errors.add(fieldError.getDefaultMessage());
                }
                response.put("message", "There was an error while registering the book loan");
                response.put("details", errors);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            bookLoanService.save(bookLoanDTO, null);
        } catch (NoSuchElementException e) {
            response.put("message", e.getMessage());
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("message", "There was an error while registering the book loan");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "The boOk loan was registered successfully");
        response.put("details", null);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Modify book loan values")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book loan updated successfully", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            @ApiResponse(responseCode = "400", description = "There was an error while updating the book loan",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    }),
            // @ApiResponse(responseCode = "401", description = "", content = @Content),
            // @ApiResponse(responseCode = "403", description = "", content = @Content),
            @ApiResponse(responseCode = "404", description = "Book loan not found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            @ApiResponse(responseCode = "500", description = "There was an error while updating the book loan",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    @PutMapping(value = "/book-loans/{id}", produces = "application/json")
    public ResponseEntity<?> update(@Valid @RequestBody BookLoanDTO bookLoanDTO, BindingResult result,
                                    @PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!id.matches(ID_REGEXP)) {
                response.put("message", "Invalid book loan ID");
                response.put("details", List.of());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            if (result.hasErrors()) {
                List<String> errors = new ArrayList<>();
                for (FieldError fieldError : result.getFieldErrors()) {
                    errors.add(fieldError.getDefaultMessage());
                }
                response.put("message", "There was an error while updating book loan values");
                response.put("details", errors);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            bookLoanService.save(bookLoanDTO, Long.parseLong(id));
        } catch (NoSuchElementException e) {
            response.put("message", e.getMessage());
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("message", "There was an error while updating book loan values");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "The book loan was updated successfully");
        response.put("details", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Modify book loan status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book loan status updated successfully", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid book loan ID / " +
                    "There was an error while updating the book loan status",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    }),
            // @ApiResponse(responseCode = "401", description = "", content = @Content),
            // @ApiResponse(responseCode = "403", description = "", content = @Content),
            @ApiResponse(responseCode = "404", description = "Book copy not found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            @ApiResponse(responseCode = "500", description = "There was an error while updating the book loan status",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    @PutMapping(value = "/book-loans/{id}/status", produces = "application/json")
    public ResponseEntity<?> updateStatus(@Valid @RequestBody UpdateStatusDTO updateStatusDTO,
                                          BindingResult result, @PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        String msg = "";
        try {
            if (!id.matches(ID_REGEXP)) {
                response.put("message", "Invalid book loan ID");
                response.put("details", List.of());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            if (result.hasErrors()) {
                List<String> errors = new ArrayList<>();
                for (FieldError fieldError : result.getFieldErrors()) {
                    errors.add(fieldError.getDefaultMessage());
                }
                response.put("message", "There was an error while updating book loan status");
                response.put("details", errors);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            msg = bookLoanService.updateStatus(Long.parseLong(id), updateStatusDTO);
        } catch (NoSuchElementException e) {
            response.put("message", e.getMessage());
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("message", "There was an error while updating book loan status");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", msg);
        response.put("details", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

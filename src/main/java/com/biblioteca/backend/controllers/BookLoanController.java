package com.biblioteca.backend.controllers;

import com.biblioteca.backend.models.dtos.BookLoanDTO;
import com.biblioteca.backend.models.dtos.LibraryDTO;
import com.biblioteca.backend.models.dtos.UpdateStatusDTO;
import com.biblioteca.backend.models.entities.BookLoan;
import com.biblioteca.backend.models.entities.Library;
import com.biblioteca.backend.models.projections.BookLoanView;
import com.biblioteca.backend.services.IBookLoanService;
import com.biblioteca.backend.services.ILibraryService;
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
// @Api(value = "book", description = "Library controller endpoints")
public class BookLoanController {
    private final IBookLoanService bookLoanService;

    public BookLoanController(IBookLoanService bookLoanService) {
        this.bookLoanService = bookLoanService;
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
    @GetMapping(value = "/book-loans", produces = "application/json")
    public ResponseEntity<?> fetchAll() {
        Map<String, Object> response = new HashMap<>();
        List<BookLoan> bookLoans;
        try {
            bookLoans = bookLoanService.findAll();
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving book loans");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("data", bookLoans);
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
    @GetMapping(value = "/book-loans/view", produces = "application/json")
    public ResponseEntity<?> fetchAllWithView() {
        Map<String, Object> response = new HashMap<>();
        List<BookLoanView> bookLoans;
        try {
            bookLoans = bookLoanService.findAllWithView();
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving book loans");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("data", bookLoans);
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
    @GetMapping(value = "/book-loans/{id}", produces = "application/json")
    public ResponseEntity<?> getOne(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        BookLoan bookLoan;
        try {
            if (!id.matches("^\\d+$")) {
                response.put("message", "Invalid book loan ID");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            bookLoan = bookLoanService.findById(Long.parseLong(id)).orElseThrow();
        } catch (NoSuchElementException e) {
            response.put("message", "Book loan not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the book loan by id");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("data", bookLoan);
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
    @GetMapping(value = "/book-loans/{id}/view", produces = "application/json")
    public ResponseEntity<?> getOneWithView(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        BookLoanView bookLoan;
        try {
            if (!id.matches("^\\d+$")) {
                response.put("message", "Invalid book loan ID");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            bookLoan = bookLoanService.findByIdWithView(Long.parseLong(id)).orElseThrow();
        } catch (NoSuchElementException e) {
            response.put("message", "Book loan not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the book loan by id");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("data", bookLoan);
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
    @GetMapping(value = "/book-loans/{code}/code", produces = "application/json")
    public ResponseEntity<?> getOneByCode(@PathVariable String code) {
        Map<String, Object> response = new HashMap<>();
        BookLoan bookLoan;
        try {
            bookLoan = bookLoanService.findByCode(code).orElseThrow();
        } catch (NoSuchElementException e) {
            response.put("message", "Book loan not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the book loan by code");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("data", bookLoan);
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
    @GetMapping(value = "/book-loans/{code}/code/view", produces = "application/json")
    public ResponseEntity<?> getOneByCodeWithView(@PathVariable String code) {
        Map<String, Object> response = new HashMap<>();
        BookLoanView bookLoan;
        try {
            bookLoan = bookLoanService.findByCodeWithView(code).orElseThrow();
        } catch (NoSuchElementException e) {
            response.put("message", "Book loan not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the book loan by code");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("data", bookLoan);
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
    @GetMapping(value = "/book-loans/{status}/status", produces = "application/json")
    public ResponseEntity<?> fetchByStatus(@PathVariable String status) {
        Map<String, Object> response = new HashMap<>();
        List<BookLoan> bookLoans;
        try {
            bookLoans = bookLoanService.findByStatus(status);
        } catch (NoSuchElementException e) {
            response.put("message", "Book loan not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the book loans by status");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("data", bookLoans);
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
    @GetMapping(value = "/book-loans/{status}/status/view", produces = "application/json")
    public ResponseEntity<?> findByStatusWithView(@PathVariable String status) {
        Map<String, Object> response = new HashMap<>();
        List<BookLoanView> bookLoans;
        try {
            bookLoans = bookLoanService.findByStatusWithView(status);
        } catch (NoSuchElementException e) {
            response.put("message", "Book loan not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the book loans by status");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("data", bookLoans);
        response.put("message", "Data found");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
    @ApiOperation(value = "Método de registro de categorias", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Categoría registrada"),
            @ApiResponse(code = 400, message = "La categoría ya existe"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = "El nombre de la " +
            "book es requerido"),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de registrar la categoría. " +
                    "Inténtelo mas tarde")})
    */
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
                response.put("message", errors);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            bookLoanService.save(bookLoanDTO, null);
        } catch (NoSuchElementException e) {
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("message", "There was an error while registering the book loan");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "The boOk loan was registered successfully");
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

    @PutMapping(value = "/book-loans/{id}", produces = "application/json")
    public ResponseEntity<?> update(@Valid @RequestBody BookLoanDTO bookLoanDTO, BindingResult result,
                                    @PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!id.matches("^\\d+$")) {
                response.put("message", "Invalid book loan ID");
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
            bookLoanService.save(bookLoanDTO, Long.parseLong(id));
        } catch (NoSuchElementException e) {
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("message", "There was an error while updating book loan values");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "The book loan was updated successfully");
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

    @PutMapping(value = "/book-loans/{id}/status", produces = "application/json")
    public ResponseEntity<?> updateStatus(@Valid @RequestBody UpdateStatusDTO updateStatusDTO,
                                          BindingResult result, @PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        String msg = "";
        try {
            if (!id.matches("^\\d+$")) {
                response.put("message", "Invalid book loan ID");
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
            msg = bookLoanService.updateStatus(Long.parseLong(id), updateStatusDTO);
        } catch (NoSuchElementException e) {
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("message", "There was an error while updating book loan status");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", msg);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

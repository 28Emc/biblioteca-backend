package com.biblioteca.backend.controllers;

import com.biblioteca.backend.models.dtos.BookDTO;
import com.biblioteca.backend.models.dtos.UpdateStatusDTO;
import com.biblioteca.backend.models.entities.Book;
import com.biblioteca.backend.services.IBookService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@CrossOrigin(origins = {"*", "http://localhost:4200"})
@RestController
// @Api(value = "book", description = "Book controller endpoints")
public class BookController {
    private final IBookService bookService;

    public BookController(IBookService bookService) {
        this.bookService = bookService;
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
    @GetMapping(value = "/books", produces = "application/json")
    public ResponseEntity<?> fetchAll() {
        Map<String, Object> response = new HashMap<>();
        List<Book> books;
        try {
            books = bookService.findAll();
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving books");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("data", books);
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
    @GetMapping(value = "/books/{id}", produces = "application/json")
    public ResponseEntity<?> getOne(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        Book book;
        try {
            if (!id.matches("^\\d+$")) {
                response.put("message", "Invalid Book ID");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            book = bookService.findById(Long.parseLong(id)).orElseThrow();
        } catch (NoSuchElementException e) {
            response.put("message", "Book no found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the book");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("data", book);
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
    @GetMapping(value = "/books/{isbn}/isbn", produces = "application/json")
    public ResponseEntity<?> getOneByISBN(@PathVariable String isbn) {
        Map<String, Object> response = new HashMap<>();
        Book book;
        try {
            book = bookService.findByISBN(isbn).orElseThrow();
        } catch (NoSuchElementException e) {
            response.put("message", "Book not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the book by ISBN");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("data", book);
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

    @PostMapping(value = "/books", produces = "application/json")
    public ResponseEntity<?> create(@Valid @RequestBody BookDTO bookDTO,
                                    BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        Optional<Book> bookFound;
        try {
            if (result.hasErrors()) {
                List<String> errors = new ArrayList<>();
                for (FieldError fieldError : result.getFieldErrors()) {
                    errors.add(fieldError.getDefaultMessage());
                }
                response.put("message", errors);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            bookFound = bookService.findByTitle(bookDTO.getTitle());
            if (bookFound.isPresent()) {
                response.put("message", "Book already exists");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            bookService.save(bookDTO);
        } catch (NoSuchElementException e) {
            response.put("message", "Category not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("message", "There was an error while registering the book");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "The book was registered successfully");
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

    @PutMapping(value = "/books/{id}", produces = "application/json")
    public ResponseEntity<?> update(@Valid @RequestBody BookDTO bookDTO, BindingResult result,
                                    @PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        Book bookFound;
        try {
            if (!id.matches("^\\d+$")) {
                response.put("message", "Invalid book ID");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            bookService.findById(Long.parseLong(id)).orElseThrow();
            if (result.hasErrors()) {
                List<String> errors = new ArrayList<>();
                for (FieldError fieldError : result.getFieldErrors()) {
                    errors.add(fieldError.getDefaultMessage());
                }
                response.put("message", errors);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            /*
            else if (bookService.findByTitle(bookDTO.getTitle()).isPresent() &&
                    !bookFound.getTitle().equals(bookDTO.getTitle())) {
                response.put("message", "Book already exists");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            */
            bookService.save(bookDTO);
        } catch (NoSuchElementException e) {
            response.put("message", "Book or category not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("message", "There was an error while updating book values");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "The book was updated successfully");
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

    @PutMapping(value = "/books/{id}/status", produces = "application/json")
    public ResponseEntity<?> updateStatus(@Valid @RequestBody UpdateStatusDTO updateStatusDTO,
                                          BindingResult result, @PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        Book bookFound;
        try {
            if (!id.matches("^\\d+$")) {
                response.put("message", "Invalid Book ID");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            bookFound = bookService.findById(Long.parseLong(id)).orElseThrow();
            if (result.hasErrors()) {
                List<String> errors = new ArrayList<>();
                for (FieldError fieldError : result.getFieldErrors()) {
                    errors.add(fieldError.getDefaultMessage());
                }
                response.put("message", errors);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            bookFound.setStatus(updateStatusDTO.getStatus());
            bookService.updateStatus(bookFound);
        } catch (NoSuchElementException e) {
            response.put("message", "Book not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("message", "There was an error while updating book status");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "The book status was updated successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

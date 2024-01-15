package com.biblioteca.backend.controllers;

import com.biblioteca.backend.models.dtos.BookDTO;
import com.biblioteca.backend.models.dtos.UpdateStatusDTO;
import com.biblioteca.backend.models.entities.Book;
import com.biblioteca.backend.models.projections.BookView;
import com.biblioteca.backend.services.IBookService;
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
@Tag(name = "Book", description = "Book operations")
public class BookController {
    private final IBookService bookService;

    public BookController(IBookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "Fetch book list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityListSchema"))
            }),
            // @ApiResponse(responseCode = 401, description = ""),
            // @ApiResponse(responseCode = "403", description = ""),
            // @ApiResponse(responseCode = "404", description = ""),
            @ApiResponse(responseCode = "500", description = "There was an error while retrieving the books",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/books", produces = "application/json")
    public ResponseEntity<?> fetchAll() {
        Map<String, Object> response = new HashMap<>();
        List<Book> books;
        try {
            books = bookService.findAll();
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving books");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "Data found");
        response.put("details", books);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Fetch book list with custom view")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityListSchema"))
            }),
            // @ApiResponse(responseCode = 401, description = ""),
            // @ApiResponse(responseCode = "403", description = ""),
            // @ApiResponse(responseCode = "404", description = ""),
            @ApiResponse(responseCode = "500", description = "There was an error while retrieving the books",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/books/view", produces = "application/json")
    public ResponseEntity<?> fetchAllWithView() {
        Map<String, Object> response = new HashMap<>();
        List<BookView> books;
        try {
            books = bookService.findAllWithView();
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving books");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "Data found");
        response.put("details", books);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get book by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid book ID", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            // @ApiResponse(responseCode = "401", description = "", content = @Content),
            // @ApiResponse(responseCode = "403", description = "", content = @Content),
            @ApiResponse(responseCode = "404", description = "Book not found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            @ApiResponse(responseCode = "500", description = "There was an error while retrieving the book",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/books/{id}", produces = "application/json")
    public ResponseEntity<?> getOne(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        Book book;
        try {
            if (!id.matches(ID_REGEXP)) {
                response.put("message", "Invalid Book ID");
                response.put("details", List.of());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            book = bookService.findById(Long.parseLong(id)).orElseThrow();
        } catch (NoSuchElementException e) {
            response.put("message", "Book not found");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the book");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "Data found");
        response.put("details", book);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get book by ID with custom view")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid book ID", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            // @ApiResponse(responseCode = "401", description = "", content = @Content),
            // @ApiResponse(responseCode = "403", description = "", content = @Content),
            @ApiResponse(responseCode = "404", description = "Book not found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            @ApiResponse(responseCode = "500", description = "There was an error while retrieving the book",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/books/{id}/view", produces = "application/json")
    public ResponseEntity<?> getOneWithView(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        BookView book;
        try {
            if (!id.matches(ID_REGEXP)) {
                response.put("message", "Invalid Book ID");
                response.put("details", List.of());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            book = bookService.findByIdWithView(Long.parseLong(id)).orElseThrow();
        } catch (NoSuchElementException e) {
            response.put("message", "Book not found");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the book");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "Data found");
        response.put("details", book);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get book by library ID")
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
            @ApiResponse(responseCode = "404", description = "Book not found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            @ApiResponse(responseCode = "500", description = "There was an error while retrieving the book",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/books/{libraryId}/library", produces = "application/json")
    public ResponseEntity<?> fetchByLibraryId(@PathVariable String libraryId) {
        Map<String, Object> response = new HashMap<>();
        List<Book> book;
        try {
            if (!libraryId.matches(ID_REGEXP)) {
                response.put("message", "Invalid library ID");
                response.put("details", List.of());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            book = bookService.findByLibraryId(Long.parseLong(libraryId));
        } catch (NoSuchElementException e) {
            response.put("message", "Book not found");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the book by library ID");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "Data found");
        response.put("details", book);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get book by library ID with custom view")
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
            @ApiResponse(responseCode = "404", description = "Book not found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            @ApiResponse(responseCode = "500", description = "There was an error while retrieving the book",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/books/{libraryId}/library/view", produces = "application/json")
    public ResponseEntity<?> fetchByLibraryIdWithView(@PathVariable String libraryId) {
        Map<String, Object> response = new HashMap<>();
        List<BookView> book;
        try {
            if (!libraryId.matches(ID_REGEXP)) {
                response.put("message", "Invalid library ID");
                response.put("details", List.of());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            book = bookService.findByLibraryIdWithView(Long.parseLong(libraryId));
        } catch (NoSuchElementException e) {
            response.put("message", "Book not found");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the book by library ID");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "Data found");
        response.put("details", book);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get book by ISBN and library ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            // @ApiResponse(responseCode = "401", description = "", content = @Content),
            // @ApiResponse(responseCode = "403", description = "", content = @Content),
            @ApiResponse(responseCode = "404", description = "Book not found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            @ApiResponse(responseCode = "500", description = "There was an error while retrieving the book",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/books/{isbn}/isbn/{libraryId}/library", produces = "application/json")
    public ResponseEntity<?> getOneByISBNAndLibraryId(@PathVariable String isbn, @PathVariable String libraryId) {
        Map<String, Object> response = new HashMap<>();
        Book book;
        try {
            if (!libraryId.matches(ID_REGEXP)) {
                response.put("message", "Invalid library ID");
                response.put("details", List.of());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            book = bookService.findByLibraryIdAndISBN(Long.parseLong(libraryId), isbn).orElseThrow();
        } catch (NoSuchElementException e) {
            response.put("message", "Book not found");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the book by library ID and ISBN");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "Data found");
        response.put("details", book);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get book by ISBN and library ID with custom view")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            // @ApiResponse(responseCode = "401", description = "", content = @Content),
            // @ApiResponse(responseCode = "403", description = "", content = @Content),
            @ApiResponse(responseCode = "404", description = "Book not found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            @ApiResponse(responseCode = "500", description = "There was an error while retrieving the book",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/books/{isbn}/isbn/{libraryId}/library/view", produces = "application/json")
    public ResponseEntity<?> getOneByISBNWithView(@PathVariable String isbn, @PathVariable String libraryId) {
        Map<String, Object> response = new HashMap<>();
        BookView book;
        try {
            if (!libraryId.matches(ID_REGEXP)) {
                response.put("message", "Invalid library ID");
                response.put("details", List.of());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            book = bookService.findByLibraryIdAndISBNWithView(Long.parseLong(libraryId), isbn).orElseThrow();
        } catch (NoSuchElementException e) {
            response.put("message", "Book not found");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the book by library ID and ISBN");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "Data found");
        response.put("details", book);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get book by category ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            // @ApiResponse(responseCode = "401", description = "", content = @Content),
            // @ApiResponse(responseCode = "403", description = "", content = @Content),
            @ApiResponse(responseCode = "404", description = "Book not found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            @ApiResponse(responseCode = "500", description = "There was an error while retrieving the book",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/books/{categoryId}/category", produces = "application/json")
    public ResponseEntity<?> fetchByCategoryId(@PathVariable String categoryId) {
        Map<String, Object> response = new HashMap<>();
        List<Book> book;
        try {
            if (!categoryId.matches(ID_REGEXP)) {
                response.put("message", "Invalid category ID");
                response.put("details", List.of());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            book = bookService.findByCategoryId(Long.parseLong(categoryId));
        } catch (NoSuchElementException e) {
            response.put("message", "Book not found");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the books by category ID");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "Data found");
        response.put("details", book);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get book by category ID with custom view")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            // @ApiResponse(responseCode = "401", description = "", content = @Content),
            // @ApiResponse(responseCode = "403", description = "", content = @Content),
            @ApiResponse(responseCode = "404", description = "Book not found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            @ApiResponse(responseCode = "500", description = "There was an error while retrieving the book",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/books/{categoryId}/category/view", produces = "application/json")
    public ResponseEntity<?> fetchByCategoryIdWithView(@PathVariable String categoryId) {
        Map<String, Object> response = new HashMap<>();
        List<BookView> book;
        try {
            if (!categoryId.matches(ID_REGEXP)) {
                response.put("message", "Invalid category ID");
                response.put("details", List.of());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            book = bookService.findByCategoryIdWithView(Long.parseLong(categoryId));
        } catch (NoSuchElementException e) {
            response.put("message", "Book not found");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the books by category ID");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "Data found");
        response.put("details", book);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Register a book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book registered successfully", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            @ApiResponse(responseCode = "400", description = "Book already exists / " +
                    "There was an error while registering the book",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    }),
            // @ApiResponse(responseCode = "401", description = "", content = @Content),
            // @ApiResponse(responseCode = "403", description = "", content = @Content),
            @ApiResponse(responseCode = "404", description = "Category not found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            @ApiResponse(responseCode = "500", description = "There was an error while registering the book",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    @PostMapping(value = "/books", produces = "application/json")
    public ResponseEntity<?> create(@Valid @RequestBody BookDTO bookDTO, BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        Optional<Book> bookFound;
        try {
            if (result.hasErrors()) {
                List<String> errors = new ArrayList<>();
                for (FieldError fieldError : result.getFieldErrors()) {
                    errors.add(fieldError.getDefaultMessage());
                }
                response.put("message", "There was an error while registering the book");
                response.put("details", errors);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            bookFound = bookService.findByTitle(bookDTO.getTitle());
            if (bookFound.isPresent()) {
                response.put("message", "Book already exists");
                response.put("details", List.of());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            bookDTO.setId(null);
            bookService.save(bookDTO);
        } catch (NoSuchElementException e) {
            response.put("message", "Category not found");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("message", "There was an error while registering the book");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "The book was registered successfully");
        response.put("details", null);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Modify book values")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book updated successfully", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            @ApiResponse(responseCode = "400", description = "Book already exists / " +
                    "There was an error while updating the book",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    }),
            // @ApiResponse(responseCode = "401", description = "", content = @Content),
            // @ApiResponse(responseCode = "403", description = "", content = @Content),
            @ApiResponse(responseCode = "404", description = "Book not found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            @ApiResponse(responseCode = "500", description = "There was an error while updating the book",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    @PutMapping(value = "/books/{id}", produces = "application/json")
    public ResponseEntity<?> update(@Valid @RequestBody BookDTO bookDTO, BindingResult result,
                                    @PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        Book bookFound;
        try {
            if (!id.matches(ID_REGEXP)) {
                response.put("message", "Invalid book ID");
                response.put("details", List.of());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            bookService.findById(Long.parseLong(id)).orElseThrow();
            if (result.hasErrors()) {
                List<String> errors = new ArrayList<>();
                for (FieldError fieldError : result.getFieldErrors()) {
                    errors.add(fieldError.getDefaultMessage());
                }
                response.put("message", "There was an error while updating book values");
                response.put("details", errors);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            /*
            else if (bookService.findByTitle(bookDTO.getTitle()).isPresent() &&
                    !bookFound.getTitle().equals(bookDTO.getTitle())) {
                response.put("message", "Book already exists");
                response.put("details", List.of());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            */
            bookDTO.setId(id);
            bookService.save(bookDTO);
        } catch (NoSuchElementException e) {
            response.put("message", "Book, category or library not found");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("message", "There was an error while updating book values");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "The book was updated successfully");
        response.put("details", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Modify book status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book status updated successfully", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid book ID / " +
                    "There was an error while updating the book status",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    }),
            // @ApiResponse(responseCode = "401", description = "", content = @Content),
            // @ApiResponse(responseCode = "403", description = "", content = @Content),
            @ApiResponse(responseCode = "404", description = "Member not found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            @ApiResponse(responseCode = "500", description = "There was an error while updating the book status",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    @PutMapping(value = "/books/{id}/status", produces = "application/json")
    public ResponseEntity<?> updateStatus(@Valid @RequestBody UpdateStatusDTO updateStatusDTO,
                                          BindingResult result, @PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!id.matches(ID_REGEXP)) {
                response.put("message", "Invalid Book ID");
                response.put("details", List.of());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            if (result.hasErrors()) {
                List<String> errors = new ArrayList<>();
                for (FieldError fieldError : result.getFieldErrors()) {
                    errors.add(fieldError.getDefaultMessage());
                }
                response.put("message", "There was an error while updating book status");
                response.put("details", errors);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            bookService.updateStatus(Long.parseLong(id), updateStatusDTO);
        } catch (NoSuchElementException e) {
            response.put("message", "Book, category or library not found");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("message", "There was an error while updating book status");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "The book status was updated successfully");
        response.put("details", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

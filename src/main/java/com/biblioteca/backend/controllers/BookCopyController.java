package com.biblioteca.backend.controllers;

import com.biblioteca.backend.models.dtos.BookCopyDTO;
import com.biblioteca.backend.models.dtos.UpdateStatusDTO;
import com.biblioteca.backend.models.entities.Book;
import com.biblioteca.backend.models.entities.BookCopy;
import com.biblioteca.backend.models.projections.BookCopyView;
import com.biblioteca.backend.services.IBookCopyService;
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
@Tag(name = "BookCopy", description = "Book copy operations")
public class BookCopyController {
    private final IBookService bookService;
    private final IBookCopyService bookCopyService;

    public BookCopyController(IBookService bookService, IBookCopyService bookCopyService) {
        this.bookService = bookService;
        this.bookCopyService = bookCopyService;
    }

    @Operation(summary = "Fetch book copy list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityListSchema"))
            }),
            // @ApiResponse(responseCode = 401, description = ""),
            // @ApiResponse(responseCode = "403", description = ""),
            // @ApiResponse(responseCode = "404", description = ""),
            @ApiResponse(responseCode = "500", description = "There was an error while retrieving the book copies",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/book-copies", produces = "application/json")
    public ResponseEntity<?> fetchAll() {
        Map<String, Object> response = new HashMap<>();
        List<BookCopy> bookCopies;
        try {
            bookCopies = bookCopyService.findAll();
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving book copies");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "Data found");
        response.put("details", bookCopies);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Fetch book copy list with custom view")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityListSchema"))
            }),
            // @ApiResponse(responseCode = 401, description = ""),
            // @ApiResponse(responseCode = "403", description = ""),
            // @ApiResponse(responseCode = "404", description = ""),
            @ApiResponse(responseCode = "500", description = "There was an error while retrieving the book copies",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/book-copies/view", produces = "application/json")
    public ResponseEntity<?> fetchAllWithView() {
        Map<String, Object> response = new HashMap<>();
        List<BookCopyView> bookCopies;
        try {
            bookCopies = bookCopyService.findAllWithView();
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving book copies");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "Data found");
        response.put("details", bookCopies);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get book copy by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid book copy ID", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            // @ApiResponse(responseCode = "401", description = "", content = @Content),
            // @ApiResponse(responseCode = "403", description = "", content = @Content),
            @ApiResponse(responseCode = "404", description = "Book copy not found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            @ApiResponse(responseCode = "500", description = "There was an error while retrieving the book copy",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/book-copies/{id}", produces = "application/json")
    public ResponseEntity<?> getOne(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        BookCopy bookCopy;
        try {
            if (!id.matches(ID_REGEXP)) {
                response.put("message", "Invalid book copy ID");
                response.put("details", List.of());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            bookCopy = bookCopyService.findById(Long.parseLong(id)).orElseThrow();
        } catch (NoSuchElementException e) {
            response.put("message", "Book copy not found");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the book copy");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "Data found");
        response.put("details", bookCopy);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get book copy by ID with custom view")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid book copy ID", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            // @ApiResponse(responseCode = "401", description = "", content = @Content),
            // @ApiResponse(responseCode = "403", description = "", content = @Content),
            @ApiResponse(responseCode = "404", description = "Book copy not found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            @ApiResponse(responseCode = "500", description = "There was an error while retrieving the book copy",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/book-copies/{id}/view", produces = "application/json")
    public ResponseEntity<?> getOneWithView(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        BookCopyView bookCopy;
        try {
            if (!id.matches(ID_REGEXP)) {
                response.put("message", "Invalid book copy ID");
                response.put("details", List.of());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            bookCopy = bookCopyService.getOneByIdWithView(Long.parseLong(id)).orElseThrow();
        } catch (NoSuchElementException e) {
            response.put("message", "Book copy not found");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the book copy");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "Data found");
        response.put("details", bookCopy);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get book copy by ISBN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            // @ApiResponse(responseCode = "401", description = "", content = @Content),
            // @ApiResponse(responseCode = "403", description = "", content = @Content),
            @ApiResponse(responseCode = "404", description = "Book copy not found", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
            }),
            @ApiResponse(responseCode = "500", description = "There was an error while retrieving the book copy",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @GetMapping(value = "/book-copies/isbn/{isbn}", produces = "application/json")
    public ResponseEntity<?> fetchByISBN(@PathVariable String isbn) {
        Map<String, Object> response = new HashMap<>();
        List<BookCopy> bookCopyList;
        try {
            bookCopyList = bookCopyService.findByISBN(isbn);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the book copies by ISBN");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "Data found");
        response.put("details", bookCopyList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Register a book copy")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book copy registered successfully", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            @ApiResponse(responseCode = "400", description = "There was an error while registering the book copy",
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
            @ApiResponse(responseCode = "500", description = "There was an error while registering the book copy",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    @PostMapping(value = "/book-copies", produces = "application/json")
    public ResponseEntity<?> create(@Valid @RequestBody BookCopyDTO bookCopyDTO,
                                    BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        Book bookFound;
        try {
            if (result.hasErrors()) {
                List<String> errors = new ArrayList<>();
                for (FieldError fieldError : result.getFieldErrors()) {
                    errors.add(fieldError.getDefaultMessage());
                }
                response.put("message", "There was an error while registering the book copy");
                response.put("details", errors);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            bookFound = bookService
                    .findByLibraryIdAndISBN(bookCopyDTO.getLibraryId(), bookCopyDTO.getISBN())
                    .orElseThrow();
            bookCopyService.save(bookFound, bookCopyDTO.getQuantity());
        } catch (NoSuchElementException e) {
            response.put("message", "Book or library not found");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("message", "There was an error while registering the book copy");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "The book copy was registered successfully");
        response.put("details", null);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Modify book copy status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book copy status updated successfully", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid book copy ID / " +
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
            @ApiResponse(responseCode = "500", description = "There was an error while updating the book copy status",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    @PutMapping(value = "/book-copies/{id}/status", produces = "application/json")
    public ResponseEntity<?> updateStatus(@Valid @RequestBody UpdateStatusDTO updateStatusDTO,
                                          BindingResult result, @PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!id.matches(ID_REGEXP)) {
                response.put("message", "Invalid Book copy ID");
                response.put("details", List.of());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            bookCopyService.findById(Long.parseLong(id)).orElseThrow();
            if (result.hasErrors()) {
                List<String> errors = new ArrayList<>();
                for (FieldError fieldError : result.getFieldErrors()) {
                    errors.add(fieldError.getDefaultMessage());
                }
                response.put("message", "There was an error while updating book copy status");
                response.put("details", errors);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            bookCopyService.updateStatus(Long.parseLong(id), updateStatusDTO);
        } catch (NoSuchElementException e) {
            response.put("message", "Book copy not found");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("message", "There was an error while updating book copy status");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "The book copy status was updated successfully");
        response.put("details", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Delete book copy by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book copy deleted successfully", content = {
                    @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(ref = "#/components/schemas/responseEntityObjectSchema"))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid book copy ID / " +
                    "Invalid book copy status, must be 'D' / There was an error while deleting the book copy",
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
            @ApiResponse(responseCode = "500", description = "There was an error while deleting the book copy",
                    content = {
                            @Content(mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(ref = "#/components/schemas/responseEntityErrorSchema"))
                    })
    })
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    @DeleteMapping(value = "/book-copies/{id}", produces = "application/json")
    public ResponseEntity<?> delete(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        BookCopy bookCopy;
        try {
            if (!id.matches(ID_REGEXP)) {
                response.put("message", "Invalid Book copy ID");
                response.put("details", List.of());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            bookCopy = bookCopyService.findById(Long.parseLong(id)).orElseThrow();
            if (!bookCopy.getStatus().equals("D")) {
                response.put("message", "Invalid Book copy status, must be D");
                response.put("details", List.of());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            bookCopyService.delete(Long.parseLong(id));
        } catch (NoSuchElementException e) {
            response.put("message", "Book copy not found");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("message", "There was an error while deleting book copy status");
            response.put("details", List.of());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "The book copy status was deleted successfully");
        response.put("details", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

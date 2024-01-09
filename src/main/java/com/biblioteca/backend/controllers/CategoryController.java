package com.biblioteca.backend.controllers;

import com.biblioteca.backend.models.dtos.CategoryDTO;
import com.biblioteca.backend.models.dtos.CategoryStatusDTO;
import com.biblioteca.backend.models.entities.Category;
import com.biblioteca.backend.services.ICategoryService;
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
// @Api(value = "category", description = "Operaciones referentes a las categorías de libros")
public class CategoryController {
    private final ICategoryService categoriaService;

    public CategoryController(ICategoryService categoriaService) {
        this.categoriaService = categoriaService;
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
    @GetMapping(value = "/categories", produces = "application/json")
    public ResponseEntity<?> fetchAll() {
        Map<String, Object> response = new HashMap<>();
        List<Category> categories;
        try {
            categories = categoriaService.findAll();
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the categories");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("data", categories);
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
    @GetMapping(value = "/categories/{id}", produces = "application/json")
    public ResponseEntity<?> getOne(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        Category category;
        try {
            if (!id.matches("^\\d+$")) {
                response.put("message", "Invalid Category ID");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            category = categoriaService.findById(Long.parseLong(id)).orElseThrow();
        } catch (NoSuchElementException e) {
            response.put("message", "Category no found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "There was an error while retrieving the category");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("data", category);
        response.put("message", "Data found");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
    @ApiOperation(value = "Método de registro de categorias", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Categoría registrada"),
            @ApiResponse(code = 400, message = "La categoría ya existe"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = "El nombre de la " +
            "category es requerido"),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de registrar la categoría. " +
                    "Inténtelo mas tarde")})
    */
    // @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")

    @PostMapping(value = "/categories", produces = "application/json")
    public ResponseEntity<?> create(@Valid @RequestBody CategoryDTO categoriaDTO,
                                    BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        Optional<Category> categoryFound;
        try {
            if (result.hasErrors()) {
                List<String> errors = new ArrayList<>();
                for (FieldError fieldError : result.getFieldErrors()) {
                    errors.add(fieldError.getDefaultMessage());
                }
                response.put("message", errors);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            categoryFound = categoriaService.findByName(categoriaDTO.getName());
            if (categoryFound.isPresent()) {
                response.put("message", "Category alreqdy exists");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            Category category = new Category();
            category.setName(categoriaDTO.getName());
            categoriaService.save(category);
        } catch (DataIntegrityViolationException e) {
            response.put("message", "There was an error while registering the category");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "The category was registered successfully");
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

    @PutMapping(value = "/categories/{id}", produces = "application/json")
    public ResponseEntity<?> update(@Valid @RequestBody CategoryDTO categoriaDTO, BindingResult result,
                                    @PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        Category categoryFound;
        try {
            if (!id.matches("^\\d+$")) {
                response.put("message", "Invalid Category ID");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            categoryFound = categoriaService.findById(Long.parseLong(id)).orElseThrow();
            if (result.hasErrors()) {
                List<String> errors = new ArrayList<>();
                for (FieldError fieldError : result.getFieldErrors()) {
                    errors.add(fieldError.getDefaultMessage());
                }
                response.put("message", errors);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else if (categoriaService.findByName(categoriaDTO.getName()).isPresent() &&
                    !categoryFound.getName().equals(categoriaDTO.getName())) {
                response.put("message", "Category already exists");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            categoryFound.setName(categoriaDTO.getName());
            categoriaService.save(categoryFound);
        } catch (NoSuchElementException e) {
            response.put("message", "Category not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("message", "There was an error while updating category values");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "The category was updated successfully");
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

    @PutMapping(value = "/categories/{id}/status", produces = "application/json")
    public ResponseEntity<?> updateStatus(@Valid @RequestBody CategoryStatusDTO categoriaStatusDTO,
                                          BindingResult result, @PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        Category categoryFound;
        try {
            if (!id.matches("^\\d+$")) {
                response.put("message", "Invalid Category ID");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            categoryFound = categoriaService.findById(Long.parseLong(id)).orElseThrow();
            if (result.hasErrors()) {
                List<String> errors = new ArrayList<>();
                for (FieldError fieldError : result.getFieldErrors()) {
                    errors.add(fieldError.getDefaultMessage());
                }
                response.put("message", errors);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            categoryFound.setStatus(categoriaStatusDTO.getStatus());
            categoriaService.save(categoryFound);
            // TODO: UPDATE BOOK STATUSES BY CATEGORY ID
        } catch (NoSuchElementException e) {
            response.put("message", "Category not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("message", "There was an error while updating category status");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("message", "The category status was updated successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

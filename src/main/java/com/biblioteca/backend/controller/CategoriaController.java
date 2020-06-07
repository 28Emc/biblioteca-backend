package com.biblioteca.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import com.biblioteca.backend.model.Categoria;
import com.biblioteca.backend.service.ICategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@CrossOrigin(origins = { "*", "http://localhost:4200" })
@RestController
@Api(value = "categoria", description = "Operaciones referentes a las categorías de libros")
public class CategoriaController {

    @Autowired
    private ICategoriaService categoriaService;

    @ApiOperation(value = "Método de listado de categorias", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Categorias encontrados"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar las categorias. Inténtelo mas tarde") })
    @GetMapping(value = "/categorias", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    public ResponseEntity<?> listarCategorias() {
        Map<String, Object> response = new HashMap<>();
        List<Categoria> categorias = null;
        try {
            categorias = categoriaService.findAll();
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de buscar las categorias!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        response.put("categorias", categorias);
        response.put("mensaje", "Categorias encontradas!");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.FOUND);
    }

    @ApiOperation(value = "Método de consulta de categoría por su id", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Categoría encontrada"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar la categoría. Inténtelo mas tarde") })
    @GetMapping(value = "/categorias/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    public ResponseEntity<?> buscarCategoria(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Categoria categoria = null;
        try {
            categoria = categoriaService.findById(id).get();
            if (categoria == null) {
                response.put("mensaje",
                        "La categoría con ID: ".concat(id.toString().concat(" no existe en la base de datos!")));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }
        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, la categoría no existe!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de buscar la categoría!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("categoria", categoria);
        response.put("mensaje", "Categoría encontrada!");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.FOUND);
    }

    @ApiOperation(value = "Método de registro de categorias", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Categoría registrada"),
            @ApiResponse(code = 400, message = "Lo sentimos, la categoría ya existe"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de registrar la categoría. Inténtelo mas tarde") })
    @PostMapping(value = "/categorias", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    public ResponseEntity<?> crearCategoria(@RequestBody Categoria categoria) {
        Map<String, Object> response = new HashMap<>();
        Optional<Categoria> categoriaEncontrada = null;
        try {
            categoriaEncontrada = categoriaService.findByNombre(categoria.getNombre());
            if (categoriaEncontrada.isPresent()) {
                response.put("mensaje", "Lo sentimos, la categoría ya existe!");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            } else {
                categoria.setActivo(true);
                categoriaService.save(categoria);
            }
        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de registrar la categoría!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Categoría registrada!");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de actualización de categorias", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Categoría actualizada"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "La categoría no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de actualizar la categoría. Inténtelo mas tarde") })
    @PutMapping(value = "/categorias/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    public ResponseEntity<?> editarCategoria(@RequestBody Categoria categoria, @PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Categoria categoriaEncontrada = null;
        try {
            categoriaEncontrada = categoriaService.findById(id).get();
            if (categoriaEncontrada == null) {
                response.put("mensaje",
                        "La categoría con ID: ".concat(id.toString().concat(" no existe en la base de datos!")));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }
            categoriaEncontrada.setNombre(categoria.getNombre());
            // categoriaEncontrada.setLibros(categoria.getLibros());
            categoriaService.save(categoriaEncontrada);
        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, la categoría no existe!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de actualizar la categoría!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Categoría actualizada!");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de deshabilitación de la categoría mediante el id", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Categoría deshabilitada"),
            @ApiResponse(code = 201, message = " "), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "La categoría existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de deshabilitar la categoría. Inténtelo mas tarde") })
    @PutMapping(value = "/categorias/{id}/deshabilitar", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    public ResponseEntity<?> deshabilitarCategoria(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Categoria categoriaEncontrada = null;
        try {
            categoriaEncontrada = categoriaService.findById(id).get();
            if (categoriaEncontrada == null) {
                response.put("mensaje",
                        "La categoría con ID: ".concat(id.toString().concat(" no existe en la base de datos!")));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }
            // TODO: DESHABILITAR LOS "HIJOS" (LIBROS) ANTES DE DESHABILITAR EL "PADRE"
            // (CATEGORÍA) - VER CASO DE LOCALES CON EMPRESA
            categoriaEncontrada.setActivo(false);
            categoriaService.save(categoriaEncontrada);
        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, la categoría no existe!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de deshabilitar la categoría!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Categoría deshabilitada!");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

}
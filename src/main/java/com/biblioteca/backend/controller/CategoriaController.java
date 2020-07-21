package com.biblioteca.backend.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.biblioteca.backend.model.Categoria.Categoria;
import com.biblioteca.backend.model.Categoria.DTO.CategoriaDTO;
import com.biblioteca.backend.model.Libro.Libro;
import com.biblioteca.backend.service.ICategoriaService;
import com.biblioteca.backend.service.ILibroService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
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

import javax.validation.Valid;

@CrossOrigin(origins = {"*", "http://localhost:4200"})
@RestController
@Api(value = "categoria", description = "Operaciones referentes a las categorías de libros")
public class CategoriaController {

    @Autowired
    private ICategoriaService categoriaService;

    @Autowired
    private ILibroService libroService;

    @ApiOperation(value = "Método de listado de categorias", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Categorias encontrados"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar las categorias. " +
                    "Inténtelo mas tarde")})
    @GetMapping(value = "/categorias", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    public ResponseEntity<?> listarCategorias() {
        Map<String, Object> response = new HashMap<>();
        List<Categoria> categorias;
        try {
            categorias = categoriaService.findAll();
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de buscar las categorias");
            //response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("categorias", categorias);
        response.put("mensaje", "Categorias encontradas");
        return new ResponseEntity<>(response, HttpStatus.FOUND);
    }

    @ApiOperation(value = "Método de consulta de categoría por su id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Categoría encontrada"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar la categoría. " +
                    "Inténtelo mas tarde")})
    @GetMapping(value = "/categorias/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    public ResponseEntity<?> buscarCategoria(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        Categoria categoria;
        try {
            if (id.matches("^\\d+$")) {
                categoria = categoriaService.findById(Long.parseLong(id)).orElseThrow();
            } else {
                response.put("mensaje", "Lo sentimos, el id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (NoSuchElementException e) {
            response.put("mensaje", "La categoría no existe");
            //response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de buscar la categoría");
            //response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("categoria", categoria);
        response.put("mensaje", "Categoría encontrada");
        return new ResponseEntity<>(response, HttpStatus.FOUND);
    }

    @ApiOperation(value = "Método de registro de categorias", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Categoría registrada"),
            @ApiResponse(code = 400, message = "La categoría ya existe"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = "El nombre de la " +
            "categoria es requerido"),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de registrar la categoría. " +
                    "Inténtelo mas tarde")})
    @PostMapping(value = "/categorias", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    public ResponseEntity<?> crearCategoria(@RequestBody /*@Valid*/ CategoriaDTO categoriaDTO/*,
                                            BindingResult result*/) {
        Map<String, Object> response = new HashMap<>();
        Optional<Categoria> categoriaEncontrada;
        try {
            // TODO: AQUI VA LA VALIDACIÓN DEL OBJETO (@NOTBLANK, @VALID)
            categoriaEncontrada = categoriaService.findByNombre(categoriaDTO.getNombre());
            if (categoriaEncontrada.isPresent()) {
                response.put("mensaje", "La categoría ya existe");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                // TODO: @NOTBLANK
            } else if (categoriaDTO.getNombre() == null || categoriaDTO.getNombre().isBlank()) {
                response.put("mensaje",
                        "El nombre de la categoría es requerido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else {
                Categoria categoria = new Categoria();
                categoria.setNombre(categoriaDTO.getNombre());
                categoria.setActivo(true);
                categoriaService.save(categoria);
            }
        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de registrar la categoría");
            //response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Categoría registrada");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de actualización de categorias", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Categoría actualizada"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "La categoría no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de actualizar la categoría. " +
                    "Inténtelo mas tarde")})
    @PutMapping(value = "/categorias/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    public ResponseEntity<?> editarCategoria(@RequestBody CategoriaDTO categoriaDTO, @PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        Categoria categoriaEncontrada;
        try {
            // TODO: AQUI VA LA VALIDACIÓN DEL OBJETO (@NOTBLANK, @VALID)
            if (id.matches("^\\d+$")) {
                categoriaEncontrada = categoriaService.findById(Long.parseLong(id)).orElseThrow();
            } else {
                response.put("mensaje", "Lo sentimos, el id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            // TODO: @NOTBLANK
            if (categoriaDTO.getNombre() == null || categoriaDTO.getNombre().isBlank()) {
                response.put("mensaje",
                        "El nombre de la categoría es requerido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else if (categoriaService.findByNombre(categoriaDTO.getNombre()).isPresent() &&
                    !categoriaEncontrada.getNombre().equals(categoriaDTO.getNombre())) {
                response.put("mensaje",
                        "La categoría ya existe");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            categoriaEncontrada.setNombre(categoriaDTO.getNombre());
            categoriaService.save(categoriaEncontrada);
        } catch (NoSuchElementException e) {
            response.put("mensaje", "La categoría no existe");
            //response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de actualizar la categoría");
            //response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Categoría actualizada");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de deshabilitación de la categoría mediante el id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Categoría deshabilitada"),
            @ApiResponse(code = 201, message = " "), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "La categoría no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de deshabilitar la categoría. " +
                    "Inténtelo mas tarde")})
    @PutMapping(value = "/categorias/{id}/deshabilitar", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    public ResponseEntity<?> deshabilitarCategoria(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        Categoria categoriaEncontrada;
        List<Libro> librosTotales;
        List<Libro> librosActivos = new ArrayList<>();
        List<Libro> librosInactivos = new ArrayList<>();
        try {
            if (id.matches("^\\d+$")) {
                categoriaEncontrada = categoriaService.findById(Long.parseLong(id)).orElseThrow();
            } else {
                response.put("mensaje", "Lo sentimos, el id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            if (!categoriaEncontrada.isActivo()) {
                response.put("mensaje", "La categoría ya está deshabilitada");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            // SE DESHABILITAN LOS "HIJOS" (LIBROS) ANTES DE DESHABILITAR EL "PADRE"
            // (CATEGORÍA) - LÓGICA SIMILAR DE LOCALES CON EMPRESA (LOCALCONTROLLER)
            // NOTA: NUEVAMENTE, REVISAR SI ESTE MÉTODO ES EFICIENTE
            librosTotales = libroService.findByCategoria(categoriaEncontrada.getNombre());
            librosTotales.forEach(l -> {
                if (l.isActivo()) {
                    librosActivos.add(l);
                    l.setActivo(false);
                    libroService.save(l);
                } else {
                    librosInactivos.add(l);
                }
            });
            //response.put("librosDelLocalActivos", librosActivos.size());
            //response.put("librosDelLocalInactivos", librosInactivos.size());
            categoriaEncontrada.setActivo(false);
            categoriaService.save(categoriaEncontrada);
        } catch (NoSuchElementException e) {
            response.put("mensaje", "La categoría no existe");
            //response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de deshabilitar la categoría");
            //response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Categoría deshabilitada");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de habilitación de la categoría mediante el id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Categoría habilitada"),
            @ApiResponse(code = 201, message = " "), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "La categoría no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de habilitar la categoría. " +
                    "Inténtelo mas tarde")})
    @PutMapping(value = "/categorias/{id}/habilitar", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    public ResponseEntity<?> habilitarCategoria(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        Categoria categoriaEncontrada;
        List<Libro> librosTotales;
        List<Libro> librosActivos = new ArrayList<>();
        List<Libro> librosInactivos = new ArrayList<>();
        try {
            if (id.matches("^\\d+$")) {
                categoriaEncontrada = categoriaService.findById(Long.parseLong(id)).orElseThrow();
            } else {
                response.put("mensaje", "Lo sentimos, el id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            if (categoriaEncontrada.isActivo()) {
                response.put("mensaje", "La categoría ya está habilitada");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            // SE HABILITAN LOS "HIJOS" (LIBROS) ANTES DE HABILITAR EL "PADRE"
            // (CATEGORÍA) - LÓGICA SIMILAR DE LOCALES CON EMPRESA (LOCALCONTROLLER)
            // NOTA: NUEVAMENTE, REVISAR SI ESTE MÉTODO ES EFICIENTE
            librosTotales = libroService.findByCategoria(categoriaEncontrada.getNombre());
            librosTotales.forEach(l -> {
                if (!l.isActivo()) {
                    librosInactivos.add(l);
                    l.setActivo(true);
                    libroService.save(l);
                } else {
                    librosActivos.add(l);
                }
            });
            //response.put("librosDelLocalActivos", librosActivos.size());
            //response.put("librosDelLocalInactivos", librosInactivos.size());
            categoriaEncontrada.setActivo(true);
            categoriaService.save(categoriaEncontrada);
        } catch (NoSuchElementException e) {
            response.put("mensaje", "La categoría no existe");
            //response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de habilitar la categoría");
            //response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Categoría habilitada");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
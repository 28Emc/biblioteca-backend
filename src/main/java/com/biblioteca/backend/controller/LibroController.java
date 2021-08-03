package com.biblioteca.backend.controller;

import java.util.*;
import java.util.stream.Collectors;

import com.biblioteca.backend.model.Empleado.Empleado;
import com.biblioteca.backend.model.Libro.DTO.LibroDTO;
import com.biblioteca.backend.model.Libro.Libro;
import com.biblioteca.backend.model.Local.Local;
import com.biblioteca.backend.model.Usuario.Usuario;
import com.biblioteca.backend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
@Api(value = "libro", description = "Operaciones referentes a los libros")
public class LibroController {

    @Autowired
    private ILibroService libroService;

    @Autowired
    private ICategoriaService categoriaService;

    @Autowired
    private ILocalService localService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IEmpleadoService empleadoService;

    @ApiOperation(value = "Método de listado de libros", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Libros encontrados"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar los libros. " +
                    "Inténtelo mas tarde")})
    @GetMapping(value = "/libros", produces = "application/json")
    //@PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO', 'ROLE_USUARIO')")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLEADO')")
    public ResponseEntity<?> listarLibros(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario empleadoLogueado = usuarioService.findByUsuario(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        List<Libro> libros = new ArrayList<>();
        try {
            switch (empleadoLogueado.getRol().getAuthority()) {
                case "ROLE_ADMIN":
                    libros = libroService.findAll();
                    break;
                case "ROLE_EMPLEADO":
                    Empleado empleadoFound = empleadoService.findByIdUsuario(empleadoLogueado.getId());
                    libros = libroService
                            .findAll()
                            .stream()
                            .filter(libro -> libro.getLocal().getId().equals(empleadoFound.getLocal().getId()))
                            .collect(Collectors.toList());
                    break;
            }
        } catch (NoSuchElementException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de buscar los libros");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de buscar los libros");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Libros encontrados: ".concat(String.valueOf(libros.size())));
        response.put("data", libros);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de listado de libros para el usuario", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Libros encontrados"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar los libros. " +
                    "Inténtelo mas tarde")})
    @GetMapping(value = "/biblioteca", produces = "application/json")
    //@PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO', 'ROLE_USUARIO')")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLEADO', 'ROLE_USUARIO')")
    public ResponseEntity<?> listarLibrosBiblioteca() {
        Map<String, Object> response = new HashMap<>();
        List<Libro> libros;
        try {
            libros = libroService
                    .findAll()
                    .stream()
                    .filter(Libro::isActivo)
                    .collect(Collectors.toList());
        } catch (NoSuchElementException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de buscar los libros");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de buscar los libros");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Libros encontrados");
        response.put("data", libros);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de consulta de libro por su id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Libro encontrado"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = "El id es invalido"), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar el libro." +
                    " Inténtelo mas tarde")})
    @GetMapping(value = "/libros/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLEADO')")
    public ResponseEntity<?> buscarLibro(@PathVariable String id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario empleadoLogueado = usuarioService.findByUsuario(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        Libro libro = new Libro();
        try {

            if (!id.matches("^\\d+$")) {
                response.put("message", "Lo sentimos, hubo un error a la hora de buscar el libro");
                response.put("error", "El id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            switch (empleadoLogueado.getRol().getAuthority()) {
                case "ROLE_ADMIN":
                    libro = libroService
                            .findById(Long.parseLong(id))
                            .orElseThrow();
                    break;
                case "ROLE_EMPLEADO":
                    Empleado empleadoFound = empleadoService.findByIdUsuario(empleadoLogueado.getId());
                    libro = libroService
                            .findById(Long.parseLong(id))
                            .orElseThrow();

                    if (!libro.getLocal().getId().equals(empleadoFound.getLocal().getId())) {
                        throw new Exception("No tienes acceso a este recurso");
                    }

                    break;
            }
        } catch (NoSuchElementException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de buscar el libro");
            response.put("error", "El libro no existe");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de buscar el libro");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Libro encontrado");
        response.put("data", libro);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de registro de libros", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Libro registrado"),
            @ApiResponse(code = 400, message = "El libro ya existe"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de registrar el libro. " +
                    "Inténtelo mas tarde")})
    @PostMapping(value = "/libros", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLEADO')")
    public ResponseEntity<?> crearLibro(@Valid @RequestBody LibroDTO libroDTO, BindingResult result,
                                        Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario empleadoLogueado = usuarioService.findByUsuario(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();

        try {
            if (result.hasErrors()) {
                List<String> errores = result.getFieldErrors()
                        .stream()
                        .map(error -> error.getField() + " : " + error.getDefaultMessage())
                        .collect(Collectors.toList());
                response.put("message", "Lo sentimos, hubo un error a la hora de registrar el libro");
                response.put("error", errores);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            libroService.saveAdmin(libroDTO, empleadoLogueado);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de registrar el libro");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Libro registrado!");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de actualización de libros", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Libro Actualizado"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "El libro no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de actualizar el libro. Inténtelo mas tarde")})
    @PutMapping(value = "/libros/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<?> editarLibro(@Valid @RequestBody LibroDTO libroDTO, BindingResult result,
                                         @PathVariable String id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario empleadoLogueado = usuarioService.findByUsuario(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        Libro libroEncontrado;

        try {
            if (!id.matches("^\\d+$")) {
                response.put("message", "Lo sentimos, hubo un error a la hora de buscar el libro");
                response.put("error", "El id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            if (result.hasErrors()) {
                List<String> errores = result.getFieldErrors()
                        .stream()
                        .map(error -> error.getField() + " : " + error.getDefaultMessage())
                        .collect(Collectors.toList());
                response.put("message", "Lo sentimos, hubo un error a la hora de actualizar el libro");
                response.put("error", errores);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            libroService.update(Long.valueOf(id), libroDTO, empleadoLogueado);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de actualizar el libro");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Libro Actualizado");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de deshabilitación del libro mediante el id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Libro deshabilitado"),
            @ApiResponse(code = 201, message = " "), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "La categoría no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de deshabilitar el libro." +
                    " Inténtelo mas tarde")})
    @PutMapping(value = "/libros/{id}/off", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<?> deshabilitarLibro(@PathVariable String id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario empleadoLogueado = usuarioService.findByUsuario(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();

        try {

            if (!id.matches("^\\d+$")) {
                response.put("message", "Lo sentimos, hubo un error a la hora de buscar el libro");
                response.put("error", "El id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            libroService.changeLibroState(Long.valueOf(id), false, empleadoLogueado);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de deshabilitar el libro");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Libro deshabilitado");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de habilitación del libro mediante el id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Libro habilitado"),
            @ApiResponse(code = 201, message = " "), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "La categoría no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de habilitar el libro." +
                    " Inténtelo mas tarde")})
    @PutMapping(value = "/libros/{id}/on", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<?> habilitarLibro(@PathVariable String id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario empleadoLogueado = usuarioService.findByUsuario(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();

        try {

            if (!id.matches("^\\d+$")) {
                response.put("message", "Lo sentimos, hubo un error a la hora de buscar el libro");
                response.put("error", "El id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            libroService.changeLibroState(Long.valueOf(id), true, empleadoLogueado);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de habilitar el libro");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Libro habilitado");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
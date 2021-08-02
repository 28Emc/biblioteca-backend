package com.biblioteca.backend.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import com.biblioteca.backend.model.Libro.Libro;
import com.biblioteca.backend.model.Local.DTO.LocalDTO;
import com.biblioteca.backend.model.Local.Local;
import com.biblioteca.backend.model.Usuario.Usuario;
import com.biblioteca.backend.service.IEmpresaService;
import com.biblioteca.backend.service.ILibroService;
import com.biblioteca.backend.service.ILocalService;
import com.biblioteca.backend.service.IUsuarioService;
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
@Api(value = "local", description = "Operaciones referentes a los locales")
public class LocalController {

    @Autowired
    private IEmpresaService empresaService;

    @Autowired
    private ILocalService localService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private ILibroService libroService;

    @ApiOperation(value = "Método de listado de locales", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Locales encontrados"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar los locales. " +
                    "Inténtelo mas tarde")})
    @GetMapping(value = "/locales", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> listarLocales() {
        Map<String, Object> response = new HashMap<>();
        List<Local> locales;

        try {
            locales = localService.findAll();
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de buscar los locales");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Locales encontrados: ".concat(String.valueOf(locales.size())));
        response.put("data", locales);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de consulta de local por su id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Local encontrado"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar el local. " +
                    "Inténtelo mas tarde")})
    @GetMapping(value = "/locales/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> buscarLocal(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        Local local;

        try {
            if (!id.matches("^\\d+$")) {
                response.put("message", "Lo sentimos, hubo un error a la hora de buscar el local");
                response.put("error", "El id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            local = localService.findById(Long.parseLong(id)).orElseThrow();
        } catch (NoSuchElementException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de buscar el local");
            response.put("error", "El local no existe");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de buscar el local");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Local encontrado");
        response.put("data", local);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de registro de locales", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Local registrado"),
            @ApiResponse(code = 400, message = "La dirección ya está asociada a otro local"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de registrar el local." +
                    " Inténtelo mas tarde")})
    @PostMapping(value = "/locales", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> crearLocal(@Valid @RequestBody LocalDTO localDTO, BindingResult result) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (result.hasErrors()) {
                List<String> errores = result.getFieldErrors()
                        .stream()
                        .map(error -> error.getField() + " : " + error.getDefaultMessage())
                        .collect(Collectors.toList());
                response.put("message", "Lo sentimos, hubo un error a la hora de registrar el local");
                response.put("error", errores);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            localService.save(localDTO);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de registrar el local");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Local registrado");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de actualización de locales", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Local actualizado"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "El local no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de actualizar el local." +
                    " Inténtelo mas tarde")})
    @PutMapping(value = "/locales/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> editarLocal(@Valid @RequestBody LocalDTO localDTO, BindingResult result,
                                         @PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (!id.matches("^\\d+$")) {
                response.put("message", "Lo sentimos, hubo un error a la hora de actualizar el local");
                response.put("error", "El id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            if (result.hasErrors()) {
                List<String> errores = result.getFieldErrors()
                        .stream()
                        .map(error -> error.getField() + " : " + error.getDefaultMessage())
                        .collect(Collectors.toList());
                response.put("message", "Lo sentimos, hubo un error a la hora de actualizar el local");
                response.put("error", errores);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            localService.update(Long.valueOf(id), localDTO);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de actualizar el local");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Local actualizado");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de deshabilitación del local mediante el id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Local deshabilitado"),
            @ApiResponse(code = 201, message = " "), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "El local no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de deshabilitar el local." +
                    " Inténtelo mas tarde")})
    @PutMapping(value = "/locales/{id}/off", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> deshabilitarLocal(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (!id.matches("^\\d+$")) {
                response.put("message", "Lo sentimos, hubo un error a la hora de deshabilitar el local");
                response.put("error", "El id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            localService.changeLocalState(Long.valueOf(id), false);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de deshabilitar el local");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Local deshabilitado");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de habilitación del local mediante el id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Local habilitado"),
            @ApiResponse(code = 201, message = " "), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "El local no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de habilitar el local." +
                    " Inténtelo mas tarde")})
    @PutMapping(value = "/locales/{id}/on", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> habilitarLocal(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (!id.matches("^\\d+$")) {
                response.put("message", "Lo sentimos, hubo un error a la hora de habilitar el local");
                response.put("error", "El id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            localService.changeLocalState(Long.valueOf(id), true);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de habilitar el local");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Local habilitado");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
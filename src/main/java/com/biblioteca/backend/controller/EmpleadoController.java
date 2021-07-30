package com.biblioteca.backend.controller;

import com.biblioteca.backend.model.Categoria.Categoria;
import com.biblioteca.backend.model.Categoria.DTO.CategoriaDTO;
import com.biblioteca.backend.model.Empleado;
import com.biblioteca.backend.model.Persona.DTO.PersonaDTO;
import com.biblioteca.backend.model.Usuario.DTO.UsuarioDTO;
import com.biblioteca.backend.model.Usuario.Usuario;
import com.biblioteca.backend.service.IEmpleadoService;
import com.biblioteca.backend.service.ILocalService;
import com.biblioteca.backend.service.IUsuarioService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"*", "http://localhost:4200"})
@RestController
@Api(value = "empleado", description = "Operaciones referentes a los empleados del sistema")
public class EmpleadoController {

    @Autowired
    private IEmpleadoService empleadoService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private ILocalService localService;

    @ApiOperation(value = "Método de listado de empleados", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Empleados encontrados"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar los empleados. " +
                    "Inténtelo mas tarde")})
    @GetMapping(value = "/empleados", produces = "application/json")
    //@PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> listarEmnpleados() {
        Map<String, Object> response = new HashMap<>();
        List<PersonaDTO> empleados;

        try {
            empleados = empleadoService.findAll();
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de buscar los empleados");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("data", empleados);
        response.put("message", "Empleados encontrados");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de listado de empleados activos", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Empleados encontrados"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar los empleados. " +
                    "Inténtelo mas tarde")})
    @GetMapping(value = "/empleados/on", produces = "application/json")
    //@PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> listarEmpleadosActivos() {
        Map<String, Object> response = new HashMap<>();
        List<PersonaDTO> empleados;

        try {
            empleados = empleadoService.findActivos();
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de buscar los empleados activos");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("data", empleados);
        response.put("message", "Empleados encontrados");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de consulta de empleado por su id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Empleado encontrado"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar el empleado. " +
                    "Inténtelo mas tarde")})
    @GetMapping(value = "/empleados/{id}", produces = "application/json")
    //@PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> buscarEmpleado(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        PersonaDTO empleado;

        try {

            empleado = empleadoService.findById(id);

        } catch (NoSuchElementException e) {
            response.put("message", "El empleado no existe");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de buscar el empleado");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("data", empleado);
        response.put("message", "Empleado encontrado");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de consulta de empleado por su id local y su id usuario", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Empleado encontrado"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar el empleado. " +
                    "Inténtelo mas tarde")})
    @GetMapping(value = "/empleados/{idLocal}/{idUsuario}", produces = "application/json")
    //@PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> buscarEmpleadoPorLocalYUsuario(@PathVariable Long idLocal, @PathVariable Long idUsuario) {
        Map<String, Object> response = new HashMap<>();
        PersonaDTO empleado;

        try {

            empleado = empleadoService.findByIdLocalAndIdUsuario(idLocal, idUsuario);

        } catch (NoSuchElementException e) {
            response.put("message", "El empleado no existe");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de buscar el empleado");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("data", empleado);
        response.put("message", "Empleado encontrado");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de registro de empleados", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Empleado registrado"),
            @ApiResponse(code = 400, message = "El empleado ya existe"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = "Verificar los " +
            "campos requeridos"),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de registrar el empleado. " +
                    "Inténtelo mas tarde")})
    @PostMapping(value = "/empleados", produces = "application/json")
    //@PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> crearEmpleado(@Valid @RequestBody PersonaDTO personaDTO,
                                           BindingResult result) {
        Map<String, Object> response = new HashMap<>();

        try {

            if (result.hasErrors()) {
                List<String> errores = result.getFieldErrors()
                        .stream()
                        .map(error -> error.getField() + " : " + error.getDefaultMessage())
                        .collect(Collectors.toList());
                response.put("message", errores);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            empleadoService.save(personaDTO);

        } catch (Exception e) {
            response.put("message", e.getMessage());
        }

        response.put("message", "Empleado registrado");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de actualización de empleados", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Empleado actualizado"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "El empleado no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de actualizar el empleado. " +
                    "Inténtelo mas tarde")})
    @PutMapping(value = "/empleados/{id}", produces = "application/json")
    //@PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> editarEmpleado(@Valid @RequestBody PersonaDTO personaDTO, BindingResult result,
                                             @PathVariable Long id, Authentication authentication) throws Exception {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();

        try {

            if (result.hasErrors()) {
                List<String> errores = result.getFieldErrors()
                        .stream()
                        .map(error -> error.getField() + " : " + error.getDefaultMessage())
                        .collect(Collectors.toList());
                response.put("message", errores);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            empleadoService.update(id, personaDTO, usuarioLogueado);
        } catch (Exception e) {
            response.put("message", e.getMessage());
        }

        response.put("message", "Empleado actualizado");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de deshabilitación del empleado mediante el id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Empleado deshabilitado"),
            @ApiResponse(code = 201, message = " "), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "El empleado no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de deshabilitar el empleado. " +
                    "Inténtelo mas tarde")})
    @PutMapping(value = "/empleados/{id}/off", produces = "application/json")
    //@PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> deshabilitarEmpleado(@PathVariable Long id) throws Exception {
        Map<String, Object> response = new HashMap<>();

        try {
            empleadoService.changeEmpleadoState(id, false);
        } catch (Exception e) {
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Empleado deshabilitado");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de habilitación del empleado mediante el id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Empleado habilitado"),
            @ApiResponse(code = 201, message = " "), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "El empleado no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de habilitar el empleado. " +
                    "Inténtelo mas tarde")})
    @PutMapping(value = "/empleados/{id}/on", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> habilitarEmpleado(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            empleadoService.changeEmpleadoState(id, true);
        } catch (Exception e) {
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Empleado habilitado");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

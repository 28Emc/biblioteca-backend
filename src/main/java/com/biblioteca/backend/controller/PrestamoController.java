package com.biblioteca.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.biblioteca.backend.model.Prestamo;
import com.biblioteca.backend.model.Usuario;
import com.biblioteca.backend.service.IPrestamoService;
import com.biblioteca.backend.service.IUsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@CrossOrigin(origins = { "*", "http://localhost:4200" })
@RestController
@Api(value = "prestamo", description = "Operaciones referentes a los préstamos")
public class PrestamoController {

    @Autowired
    private IPrestamoService prestamoService;

    @Autowired
    private IUsuarioService usuarioService;

    @ApiOperation(value = "Método de listado de préstamos", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Préstamos encontrados"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar los préstamos. Inténtelo mas tarde") })
    @GetMapping(value = "/prestamos", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO', 'ROLE_USUARIO')")
    public ResponseEntity<?> listarPrestamos() {
        Map<String, Object> response = new HashMap<>();
        List<Prestamo> prestamos = null;
        try {
            prestamos = prestamoService.fetchWithLibroWithUsuarioWithEmpleado();
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de buscar los préstamos!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        response.put("prestamos", prestamos);
        response.put("mensaje", "Préstamos encontrados!");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.FOUND);
    }

    @ApiOperation(value = "Método de registro de préstamos", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Préstamo registrado"), @ApiResponse(code = 400, message = " "),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de registrar el préstamo. Inténtelo mas tarde") })
    @PostMapping(value = "/prestamos", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO', 'ROLE_USUARIO')")
    public ResponseEntity<?> crearPrestamo(@RequestBody Prestamo prestamo, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByEmail(userDetails.getUsername()).get();
        Usuario empleado = null;
        Map<String, Object> response = new HashMap<>();
        try {
            // VALIDAR CAMPOS DEL PRÉSTAMO
            if (prestamo.getLibro().isActivo() && prestamo.getEmpleado().isActivo()
                    && prestamo.getUsuario().isActivo()) {
                // VALIDAR QUE ROL TIENE EL USUARIO
                switch (usuarioLogueado.getRol().getAuthority()) {
                    case "ROLE_SYSADMIN":
                        // SI ES SYSADMIN, REGISTRO EL PRÉSTAMO NORMALMENTE (VALIDAR CUALQUIER
                        // USUARIO, LOCAL, LIBRO Y EMPLEADO)
                        response.put("mensaje", "Préstamo registrado en la base de datos!");
                        break;
                    case "ROLE_ADMIN":
                        // SI ES ADMIN, REGISTRO EL PRÉSTAMO POR LOCAL (SOLO LOS EMPLEADOS
                        // PERTENECIENTES AL LOCAL DONDE TRABAJA EL ADMIN, ASI COMO EL LOCAL Y EL LIBRO)
                        if (prestamo.getEmpleado().getLocal().getId().equals(prestamo.getLibro().getLocal().getId())) {
                            response.put("mensaje", "Préstamo registrado en la base de datos!");
                        } else {
                            response.put("mensaje", "Lo sentimos, hubo un error a la hora de registrar el préstamo!");
                            response.put("error",
                                    "Solamente puede escoger entre los empleados de su local de pertenencia");
                            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
                        }
                        break;
                    case "ROLE_EMPLEADO":
                        // SI ES EMPLEADO, REGISTRA UN PRÉSTAMO SIMILAR AL ADMIN, PERO SETEA EL
                        // EMPLEADO CON SU NOMBRE
                        if (prestamo.getEmpleado().getLocal().getId().equals(prestamo.getLibro().getLocal().getId())) {
                            response.put("mensaje", "Préstamo registrado en la base de datos!");
                        } else {
                            response.put("mensaje", "Lo sentimos, hubo un error a la hora de registrar el préstamo!");
                            response.put("error", "El empleado designado no corresponde con tu perfil");
                            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
                        }
                        break;
                    default:
                        // SI ES USUARIO, REGISTRA EL PRÉSTAMO CON SU NOMBRE, EL LOCAL DEPENDE DEL
                        // LIBRO ESCOGIDO Y EL EMPLEADO, MOMENTANEAMENTE, ES "PRUEBA" (ID 1)
                        prestamo.setUsuario(usuarioLogueado);
                        empleado = usuarioService.findById(1L).get();
                        prestamo.setEmpleado(empleado);
                        // TODO: ENVÍO UN CORREO CONFIRMANDO EL PRÉSTAMO
                        response.put("mensaje", "Préstamo registrado correctamente!");
                        break;
                }
                prestamoService.save(prestamo);
            } else {
                response.put("mensaje", "Lo sentimos, hubo un error a la hora de registrar el préstamo!");
                response.put("error", "Verificar si el usuario, el empleado y/o el libro estén disponibles");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de registrar el préstamo!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }
}
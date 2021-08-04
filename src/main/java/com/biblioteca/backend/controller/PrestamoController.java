package com.biblioteca.backend.controller;

import java.util.*;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import javax.validation.Valid;

import com.biblioteca.backend.model.Prestamo.DTO.PrestamoDTO;
import com.biblioteca.backend.model.Prestamo.Prestamo;
import com.biblioteca.backend.model.Usuario.Usuario;
import com.biblioteca.backend.service.EmailService;
import com.biblioteca.backend.service.ILibroService;
import com.biblioteca.backend.service.IPrestamoService;
import com.biblioteca.backend.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

@CrossOrigin(origins = {"*", "http://localhost:4200"})
@RestController
@Api(value = "prestamo", description = "Operaciones referentes a los préstamos")
public class PrestamoController {

    @Autowired
    private IPrestamoService prestamoService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private ILibroService libroService;

    @Autowired
    private EmailService emailService;

    @Value("{spring.mail.username}")
    private String emailFrom;

    @ApiOperation(value = "Método de listado de préstamos", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Préstamos encontrados"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar los préstamos." +
                    " Inténtelo mas tarde")})
    @GetMapping(value = "/prestamos", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLEADO')")
    public ResponseEntity<?> listarPrestamos(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        List<Prestamo> prestamos;

        try {
            prestamos = prestamoService.findAllByAdmin(usuarioLogueado);
            response.put("message", "Préstamos encontrados: ".concat(String.valueOf(prestamos.size())));
            response.put("data", prestamos);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de buscar los préstamos");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de listado de préstamos completados o anulados", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Préstamos encontrados"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar los préstamos." +
                    " Inténtelo mas tarde")})
    @GetMapping(value = "/prestamos/historial", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLEADO', 'ROLE_USUARIO')")
    public ResponseEntity<?> historialUsuario(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        List<Prestamo> prestamos;

        try {
            prestamos = prestamoService
                    .findAllByAdmin(usuarioLogueado)
                    .stream()
                    .filter(prestamo -> !prestamo.getEstado().equals("E1"))
                    .collect(Collectors.toList());
            response.put("message", "Préstamos encontrados: ".concat(String.valueOf(prestamos.size())));
            response.put("data", prestamos);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de buscar los préstamos");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de listado de préstamos pendientes", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Préstamos encontrados"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar los préstamos." +
                    " Inténtelo mas tarde")})
    @GetMapping(value = "/prestamos/pendientes", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLEADO', 'ROLE_USUARIO')")
    public ResponseEntity<?> historialUsuarioPendientes(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        List<Prestamo> prestamos;

        try {
            prestamos = prestamoService
                    .findAllByAdmin(usuarioLogueado)
                    .stream()
                    .filter(prestamo -> prestamo.getEstado().equals("E1"))
                    .collect(Collectors.toList());
            response.put("message", "Préstamos encontrados: ".concat(String.valueOf(prestamos.size())));
            response.put("data", prestamos);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de buscar los préstamos");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de registro de préstamos", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Préstamo registrado"), @ApiResponse(code = 400, message = " "),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de registrar el préstamo." +
                    " Inténtelo mas tarde")})
    @PostMapping(value = "/prestamos", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLEADO', 'ROLE_USUARIO')")
    public ResponseEntity<?> crearPrestamo(@Valid @RequestBody PrestamoDTO prestamoDTO, BindingResult result,
                                           Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        Prestamo prestamo;

        try {

            if (result.hasErrors()) {
                List<String> errores = result.getFieldErrors()
                        .stream()
                        .map(error -> error.getField() + " : " + error.getDefaultMessage())
                        .collect(Collectors.toList());
                response.put("message", "Lo sentimos, hubo un error a la hora de registrar el préstamo");
                response.put("error", errores);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            prestamo = prestamoService.saveFromUser(prestamoDTO, usuarioLogueado);

            if (usuarioLogueado.getRol().getAuthority().equals("ROLE_USUARIO")) {
                Map<String, Object> model = new HashMap<>();
                model.put("titulo", "Libro Solicitado");
                model.put("from", "Biblioteca2020 " + "<" + emailFrom + ">");
                model.put("to", usuarioLogueado.getUsuario());
                model.put("prestamo", prestamo);
                model.put("subject", "Libro Solicitado | Biblioteca2020");
                emailService.enviarEmail(model);
            }

        } catch (MessagingException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de enviar el correo de confirmación");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de registrar el préstamo");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Préstamo registrado");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de confirmación de préstamo generado por el usuario", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Préstamo confirmado"), @ApiResponse(code = 400, message = " "),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de confirmar el préstamo." +
                    " Inténtelo mas tarde")})
    @PutMapping(value = "/prestamos/{id}/confirmar-prestamo", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLEADO')")
    public ResponseEntity<?> confirmarPrestamo(@PathVariable String id, @Valid @RequestBody PrestamoDTO prestamoDTO,
                                               Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).orElseThrow();
        Prestamo prestamoFound;
        Map<String, Object> response = new HashMap<>();

        try {

            if (!id.matches("^\\d+$")) {
                response.put("message", "Lo sentimos, hubo un error a la hora de buscar el préstamo");
                response.put("error", "El id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            prestamoFound = prestamoService.update(Long.valueOf(id), prestamoDTO, "E2");
            Usuario usuarioFound = usuarioService
                    .findById(prestamoFound.getIdUsuario())
                    .orElseThrow();

            if (usuarioLogueado.getRol().getAuthority().equals("ROLE_USUARIO")) {
                Map<String, Object> model = new HashMap<>();
                model.put("titulo", "Orden Confirmada");
                model.put("from", "Biblioteca2020 " + "<" + emailFrom + ">");
                model.put("to", usuarioFound.getUsuario());
                model.put("prestamo", prestamoFound);
                model.put("subject", "Orden Confirmada | Biblioteca2020");
                emailService.enviarEmail(model);
            }

        } catch (MessagingException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de enviar el correo de confirmación");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de confirmar el préstamo");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Préstamo confirmado");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de devolución de libro", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Libro devuelto"), @ApiResponse(code = 400, message = " "),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de devolver el libro." +
                    " Inténtelo mas tarde")})
    @PutMapping(value = "/prestamos/{id}/devolver-libro", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLEADO')")
    public ResponseEntity<?> devolverLibro(@PathVariable String id, @Valid @RequestBody PrestamoDTO prestamoDTO,
                                           Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).orElseThrow();
        Prestamo prestamoFound;
        Map<String, Object> response = new HashMap<>();

        try {
            if (!id.matches("^\\d+$")) {
                response.put("message", "Lo sentimos, hubo un error a la hora de buscar el préstamo");
                response.put("error", "El id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            prestamoFound = prestamoService.update(Long.valueOf(id), prestamoDTO, "E3");
            /*Usuario usuarioFound = usuarioService
                    .findById(prestamoFound.getIdUsuario())
                    .orElseThrow();*/
            // TODO: ENVÍO DE CORREO PARA PRÉSTAMO DE LIBRO DEVUELTO
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de devolver el libro");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "El libro ha sido devuelto");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de anulaciòn de prèstamo de libro", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Prèstamo anulado"), @ApiResponse(code = 400, message = " "),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = "Lo sentimos, no tienes acceso" +
            " a este recurso"),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de anular el prèstamo. " +
                    "Inténtelo mas tarde")})
    @PutMapping(value = "/prestamos/{id}/anular-prestamo", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLEADO', 'ROLE_USUARIO')")
    public ResponseEntity<?> anularPrestamo(@PathVariable String id, @Valid @RequestBody PrestamoDTO prestamoDTO,
                                            Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).orElseThrow();
        Prestamo prestamoFound;
        Map<String, Object> response = new HashMap<>();

        try {

            if (!id.matches("^\\d+$")) {
                response.put("message", "Lo sentimos, hubo un error a la hora de buscar el préstamo");
                response.put("error", "El id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            prestamoFound = prestamoService.update(Long.valueOf(id), prestamoDTO, "E4");
            /*Usuario usuarioFound = usuarioService
                    .findById(prestamoFound.getIdUsuario())
                    .orElseThrow();*/
            // TODO: ENVÍO DE CORREO PARA PRÉSTAMO DE LIBRO ANULADO
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de anular el prèstamo");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "El préstamo ha sido annulado");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
package com.biblioteca.backend.controller;

import java.util.*;
import javax.mail.MessagingException;

import com.biblioteca.backend.model.Libro.Libro;
import com.biblioteca.backend.model.Prestamo.DTO.PrestamoDTO;
import com.biblioteca.backend.model.Prestamo.Prestamo;
import com.biblioteca.backend.model.Usuario.Usuario;
import com.biblioteca.backend.service.EmailService;
import com.biblioteca.backend.service.ILibroService;
import com.biblioteca.backend.service.IPrestamoService;
import com.biblioteca.backend.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar los préstamos. Inténtelo mas tarde")})
    @GetMapping(value = "/prestamos", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO', 'ROLE_USUARIO')")
    public ResponseEntity<?> listarPrestamos(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByEmail(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        List<Prestamo> prestamos = new ArrayList<>();
        try {
            switch (usuarioLogueado.getRol().getAuthority()) {
                // MUESTRO TODOS LOS PRÉSTAMOS
                case "ROLE_SYSADMIN":
                    // MUESTRO LOS PRÉSTAMOS DEL MISMO LOCAL DEL ADMIN
                case "ROLE_ADMIN":
                    // MUESTRO LOS PRÉSTAMOS DEL MISMO LOCAL DEL EMPLEADO Y UNICAMENTE LOS ASOCIADOS
                    // CON SU ID
                case "ROLE_EMPLEADO":
                    prestamos = prestamoService.fetchWithLibroWithUsuarioWithEmpleado();
                    break;
            }
            if (prestamos.size() == 0) {
                response.put("mensaje", "No hay préstamos");
            } else {
                response.put("prestamos", prestamos);
            }
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de buscar los préstamos!");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.FOUND);
    }

    @ApiOperation(value = "Método de listado de préstamos completados o anulados", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Préstamos encontrados"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar los préstamos. Inténtelo mas tarde")})
    @GetMapping(value = "/prestamos/historial", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO', 'ROLE_USUARIO')")
    public ResponseEntity<?> historialUsuario(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByEmail(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        List<Prestamo> prestamos;
        try {
            prestamos = prestamoService.fetchByIdWithLibroWithUsuarioWithEmpleadoPerUser(usuarioLogueado.getId());
            response.put("prestamos", prestamos);
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de buscar los préstamos!");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.FOUND);
    }

    @ApiOperation(value = "Método de listado de préstamos pendientes", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Préstamos encontrados"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar los préstamos. Inténtelo mas tarde")})
    @GetMapping(value = "/prestamos/pendientes", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO', 'ROLE_USUARIO')")
    public ResponseEntity<?> historialUsuarioPendientes(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByEmail(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        List<Prestamo> prestamos;
        try {
            prestamos = prestamoService
                    .fetchByIdWithLibroWithUsuarioWithEmpleadoPerUserPendientes(usuarioLogueado.getId());
            response.put("prestamos", prestamos);
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de buscar los préstamos!");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.FOUND);
    }

    @ApiOperation(value = "Método de registro de préstamos", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Préstamo registrado"), @ApiResponse(code = 400, message = " "),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de registrar el préstamo. Inténtelo mas tarde")})
    @PostMapping(value = "/prestamos", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO', 'ROLE_USUARIO')")
    public ResponseEntity<?> crearPrestamo(@RequestBody PrestamoDTO prestamoDTO, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByEmail(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        try {
            Libro libroPrestamo = libroService.findById(prestamoDTO.getLibro().getId()).orElseThrow();
            Usuario usuarioPrestamo = usuarioService.findById(prestamoDTO.getUsuario().getId()).orElseThrow();
            Usuario empleadoPrestamo = usuarioService.findById(prestamoDTO.getEmpleado().getId()).orElseThrow();
            if (libroPrestamo.isActivo() && empleadoPrestamo.isActivo()
                    && usuarioPrestamo.isActivo()) {
                switch (usuarioLogueado.getRol().getAuthority()) {
                    case "ROLE_ADMIN":
                        // SI ES ADMIN, REGISTRO EL PRÉSTAMO POR LOCAL (SOLO LOS EMPLEADOS
                        // PERTENECIENTES AL LOCAL DONDE TRABAJA EL ADMIN, ASI COMO EL LOCAL Y EL LIBRO)
                        if (!prestamoDTO.getEmpleado().getLocal().getId().equals(prestamoDTO.getLibro().getLocal().getId())) {
                            response.put("mensaje",
                                    "Lo sentimos, hubo un error a la hora de registrar el préstamo! El empleado y el libro no pertenecen al mismo local");
                            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                        }
                        prestamoDTO.setObservaciones("Préstamo creado y confirmado por un Admin");
                        break;
                    case "ROLE_EMPLEADO":
                        // SI ES EMPLEADO, REGISTRA UN PRÉSTAMO SIMILAR AL ADMIN, PERO SETEA EL
                        // EMPLEADO CON SU NOMBRE
                        if (!prestamoDTO.getEmpleado().getLocal().getId().equals(prestamoDTO.getLibro().getLocal().getId())) {
                            response.put("mensaje",
                                    "Lo sentimos, hubo un error a la hora de registrar el préstamo! El empleado y el libro no pertenecen al mismo local.");
                            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                        }
                        prestamoDTO.setObservaciones("Préstamo creado y confirmado por un empleado");
                        prestamoDTO.setEmpleado(usuarioLogueado);
                        break;
                    default:
                        // SI ES USUARIO, REGISTRA EL PRÉSTAMO CON SU NOMBRE, EL LOCAL DEPENDE DEL
                        // LIBRO ESCOGIDO Y EL EMPLEADO, MOMENTANEAMENTE, ES "PRUEBA" (ID 1)
                        if (usuarioLogueado.getRol().getAuthority().equals("ROLE_USUARIO")) {
                            Usuario empleado = usuarioService.findById(1L).orElseThrow();
                            prestamoDTO.setObservaciones("Préstamo pendiente creado por un usuario, en espera de confirmación");
                            prestamoDTO.setUsuario(usuarioLogueado);
                            prestamoDTO.setEmpleado(empleado);
                        }
                        break;
                }

                if (libroPrestamo.getStock() <= 0) {
                    response.put("mensaje", "Lo sentimos, hubo un error a la hora de registrar el préstamo!");
                    response.put("error", "No hay stock suficiente del libro seleccionado");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                } else {
                    libroPrestamo.setStock(libroPrestamo.getStock() - 1);
                    libroService.save(libroPrestamo);
                    prestamoDTO.setLibro(libroPrestamo);
                }

                Prestamo prestamo = new Prestamo();
                prestamo.setFechaDevolucion(prestamoDTO.getFechaDevolucion());
                prestamo.setObservaciones(prestamoDTO.getObservaciones());
                prestamo.setUsuario(prestamoDTO.getUsuario());
                prestamo.setEmpleado(prestamoDTO.getEmpleado());
                prestamo.setLibro(prestamoDTO.getLibro());
                prestamoService.save(prestamo);

                if (usuarioLogueado.getRol().getAuthority().equals("ROLE_USUARIO")) {
                    Map<String, Object> model = new HashMap<>();
                    model.put("titulo", "Libro Solicitado");
                    model.put("from", "Biblioteca2020 " + "<" + emailFrom + ">");
                    model.put("to", usuarioLogueado.getEmail());
                    model.put("prestamo", prestamo);
                    model.put("subject", "Libro Solicitado | Biblioteca2020");
                    emailService.enviarEmail(model);
                }
            } else {
                response.put("mensaje",
                        "Lo sentimos, hubo un error a la hora de registrar el préstamo! Verificar si el usuario, empleado y/o libro estén disponibles.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (DataIntegrityViolationException | NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de registrar el préstamo!");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (MessagingException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de enviar el correo de confirmación!");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Préstamo registrado!");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de confirmación de préstamo generado por el usuario", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Préstamo confirmado"), @ApiResponse(code = 400, message = " "),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de confirmar el préstamo. Inténtelo mas tarde")})
    @PutMapping(value = "/prestamos/{id}/confirmar-prestamo", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    public ResponseEntity<?> confirmarPrestamo(@PathVariable Long id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByEmail(userDetails.getUsername()).orElseThrow();
        Optional<Prestamo> prestamoEncontrado;
        Map<String, Object> response = new HashMap<>();
        try {
            prestamoEncontrado = prestamoService.findById(id);
            if (prestamoEncontrado.isPresent()) {
                switch (usuarioLogueado.getRol().getAuthority()) {
                    case "ROLE_SYSADMIN":
                        // SI ES SYSADMIN, CONFIRMO EL PRÉSTAMO CON SU ID EMPLEADO
                        prestamoEncontrado.get().setObservaciones(
                                "Orden de préstamo confirmada por el Sysadmin " + usuarioLogueado.getUsuario());
                        break;
                    case "ROLE_ADMIN":
                        // SI ES ADMIN, CONFIRMO EL PRÉSTAMO CON SU ID EMPLEADO
                        prestamoEncontrado.get().setObservaciones(
                                "Orden de préstamo confirmada por el Admin " + usuarioLogueado.getUsuario()
                                        + ", del local en " + usuarioLogueado.getLocal().getDireccion());
                        break;
                    case "ROLE_EMPLEADO":
                        // SI ES EMPLEADO, CONFIRMO EL PRÉSTAMO CON SU ID EMPLEADO
                        prestamoEncontrado.get().setObservaciones(
                                "Orden de préstamo confirmada por el Empleado " + usuarioLogueado.getUsuario()
                                        + ", del local en " + usuarioLogueado.getLocal().getDireccion());
                        break;
                }
                prestamoEncontrado.get().setEmpleado(usuarioLogueado);
                prestamoService.save(prestamoEncontrado.get());
                response.put("mensaje", "Préstamo confirmado!");

                Map<String, Object> model = new HashMap<>();
                model.put("titulo", "Orden Confirmada");
                model.put("from", "Biblioteca2020 " + "<" + emailFrom + ">");
                model.put("to", prestamoEncontrado.get().getUsuario().getEmail());
                // model.put("to", "edmech25@gmail.com");
                model.put("prestamo", prestamoEncontrado);
                model.put("subject", "Orden Confirmada | Biblioteca2020");
                emailService.enviarEmail(model);
            } else {
                response.put("mensaje", "Lo sentimos, hubo un error a la hora de confirmar el préstamo!");
                response.put("error", "El préstamo no existe.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (NoSuchElementException | DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de confirmar el préstamo!");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (MessagingException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de enviar el correo de confirmación!");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de devolución de libro", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Libro devuelto"), @ApiResponse(code = 400, message = " "),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de devolver el libro. Inténtelo mas tarde")})
    @PutMapping(value = "/prestamos/{id}/devolver-libro", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    public ResponseEntity<?> devolverLibro(@PathVariable Long id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByEmail(userDetails.getUsername()).orElseThrow();
        Optional<Prestamo> prestamoEncontrado;
        Libro libro;
        Map<String, Object> response = new HashMap<>();
        try {
            prestamoEncontrado = prestamoService.findById(id);
            if (prestamoEncontrado.isPresent()) {
                switch (usuarioLogueado.getRol().getAuthority()) {
                    case "ROLE_SYSADMIN":
                        // SI ES SYSADMIN, DEVUELVO EL LIBRO CON SU ID EMPLEADO
                        prestamoEncontrado.get().setObservaciones("Libro " + prestamoEncontrado.get().getLibro().getTitulo()
                                + " devuelto por el Sysadmin " + usuarioLogueado.getUsuario());
                        break;
                    case "ROLE_ADMIN":
                        // SI ES ADMIN, DEVUELVO EL LIBRO CON SU ID EMPLEADO
                        prestamoEncontrado.get().setObservaciones("Libro " + prestamoEncontrado.get().getLibro().getTitulo()
                                + " devuelto por el Admin " + usuarioLogueado.getUsuario() + ", del local en "
                                + usuarioLogueado.getLocal().getDireccion());
                        break;
                    case "ROLE_EMPLEADO":
                        // SI ES EMPLEADO, DEVUELVO EL LIBRO CON SU ID EMPLEADO
                        prestamoEncontrado.get().setObservaciones("Libro " + prestamoEncontrado.get().getLibro().getTitulo()
                                + " devuelto por el Empleado " + usuarioLogueado.getUsuario() + ", del local en "
                                + usuarioLogueado.getLocal().getDireccion());
                        break;
                }
                libro = libroService.findById(prestamoEncontrado.get().getLibro().getId()).orElseThrow();

                if (prestamoEncontrado.get().isActivo()) {
                    response.put("mensaje", "Lo sentimos, hubo un error a la hora de devolver el libro!");
                    response.put("error", "El libro ya ha sido devuelto o anulado!.");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                } else {
                    libro.setStock(libro.getStock() + 1);
                    prestamoEncontrado.get().setActivo(true);
                }
                libroService.save(libro);

                prestamoService.save(prestamoEncontrado.get());
                response.put("mensaje", "Libro devuelto!");
            } else {
                response.put("mensaje", "Lo sentimos, hubo un error a la hora de devolver el libro!");
                response.put("error", "El préstamo no existe.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (NoSuchElementException | DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de devolver el libro!");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de anulaciòn de prèstamo de libro", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Prèstamo anulado"), @ApiResponse(code = 400, message = " "),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de anular el prèstamo. Inténtelo mas tarde")})
    @PutMapping(value = "/prestamos/{id}/anular-prestamo", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO', 'ROLE_USUARIO')")
    public ResponseEntity<?> anularPrestamo(@PathVariable Long id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByEmail(userDetails.getUsername()).orElseThrow();
        Usuario empleado;
        Optional<Prestamo> prestamoEncontrado;
        Libro libro;
        Map<String, Object> response = new HashMap<>();
        try {
            prestamoEncontrado = prestamoService.findById(id);
            if (prestamoEncontrado.isPresent()) {
                switch (usuarioLogueado.getRol().getAuthority()) {
                    case "ROLE_SYSADMIN":
                        // SI ES SYSADMIN, ANULO EL PRÈSTAMO CON SU ID EMPLEADO
                        prestamoEncontrado.get()
                                .setObservaciones("El prèstamo del libro " + prestamoEncontrado.get().getLibro().getTitulo()
                                        + " ha sido anulado por el Sysadmin " + usuarioLogueado.getUsuario());
                        break;
                    case "ROLE_ADMIN":
                        // SI ES ADMIN, ANULO EL PRÈSTAMO CON SU ID EMPLEADO
                        prestamoEncontrado.get()
                                .setObservaciones("El prèstamo del libro " + prestamoEncontrado.get().getLibro().getTitulo()
                                        + " ha sido anulado por el Admin " + usuarioLogueado.getUsuario()
                                        + ", del local en " + usuarioLogueado.getLocal().getDireccion());
                        break;
                    case "ROLE_EMPLEADO":
                        // SI ES EMPLEADO, ANULO EL PRÈSTAMO CON SU ID EMPLEADO
                        prestamoEncontrado.get()
                                .setObservaciones("El prèstamo del libro " + prestamoEncontrado.get().getLibro().getTitulo()
                                        + " ha sido anulado por el Empleado " + usuarioLogueado.getUsuario()
                                        + ", del local en " + usuarioLogueado.getLocal().getDireccion());
                        break;
                    default:
                        // SI ES USUARIO, ANULO EL PRÈSTAMO CON EL ID DE PRUEBA
                        empleado = usuarioService.findById(1L).orElseThrow();
                        prestamoEncontrado.get().setEmpleado(empleado);
                        prestamoEncontrado.get()
                                .setObservaciones("El prèstamo del libro " + prestamoEncontrado.get().getLibro().getTitulo()
                                        + " ha sido anulado por el Usuario " + usuarioLogueado.getUsuario() + ".");
                        break;
                }
                libro = libroService.findById(prestamoEncontrado.get().getLibro().getId()).orElseThrow();

                if (prestamoEncontrado.get().isActivo()) {
                    response.put("mensaje", "Lo sentimos, hubo un error a la hora de anular el prèstamo!");
                    response.put("error", "El libro ya ha sido anulado o devuelto!.");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                } else {
                    libro.setStock(libro.getStock() + 1);
                    prestamoEncontrado.get().setActivo(true);
                }
                libroService.save(libro);
                prestamoService.save(prestamoEncontrado.get());
                response.put("mensaje", "Prèstamo anulado!");
            } else {
                response.put("mensaje", "Lo sentimos, hubo un error a la hora de anular el prèstamo!");
                response.put("error", "El préstamo no existe en la BBDD.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (NoSuchElementException | DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de anular el prèstamo!");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
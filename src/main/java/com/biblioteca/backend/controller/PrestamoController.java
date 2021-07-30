package com.biblioteca.backend.controller;

import java.util.*;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import javax.validation.Valid;

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
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    public ResponseEntity<?> listarPrestamos(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        List<Prestamo> prestamos = new ArrayList<>();

        try {
            switch (usuarioLogueado.getRol().getAuthority()) {
                case "ROLE_SYSADMIN":
                    // MUESTRO TODOS LOS PRÉSTAMOS
                    prestamos = prestamoService.fetchWithLibroWithUsuarioWithEmpleado();
                    break;
                case "ROLE_ADMIN":
                case "ROLE_EMPLEADO":
                    // MUESTRO LOS PRÉSTAMOS GESTIONADOS EN EL LOCAL DEL ADMIN
                    // Y PARA LOS EMPLEADOS ES LO MISMO
                    /* TODO: REVISAR
                    Long idLocalEmp = usuarioLogueado.getLocal().getId();
                    prestamos = prestamoService.fetchByIdWithLibroWithUsuarioWithEmpleado(idLocalEmp);*/
                    break;
            }

            if (prestamos.size() == 0) {
                response.put("mensaje", "No hay préstamos");
            } else {
                response.put("prestamos", prestamos);
            }

        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de buscar los préstamos");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.FOUND);
    }

    @ApiOperation(value = "Método de listado de préstamos completados o anulados", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Préstamos encontrados"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar los préstamos." +
                    " Inténtelo mas tarde")})
    @GetMapping(value = "/prestamos/historial", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO', 'ROLE_USUARIO')")
    public ResponseEntity<?> historialUsuario(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        List<Prestamo> prestamos;

        try {
            prestamos = prestamoService.fetchByIdWithLibroWithUsuarioWithEmpleadoPerUser(usuarioLogueado.getId());

            if (prestamos.size() == 0) {
                response.put("mensaje", "Historial vacío");
            } else {
                response.put("prestamos", prestamos);
            }

        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de buscar los préstamos");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.FOUND);
    }

    @ApiOperation(value = "Método de listado de préstamos pendientes", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Préstamos encontrados"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar los préstamos." +
                    " Inténtelo mas tarde")})
    @GetMapping(value = "/prestamos/pendientes", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO', 'ROLE_USUARIO')")
    public ResponseEntity<?> historialUsuarioPendientes(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        List<Prestamo> prestamos;

        try {
            prestamos = prestamoService
                    .fetchByIdWithLibroWithUsuarioWithEmpleadoPerUserPendientes(usuarioLogueado.getId());

            if (prestamos.size() == 0) {
                response.put("mensaje", "No hay préstamos pendientes");
            } else {
                response.put("prestamos", prestamos);
            }

        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de buscar los préstamos");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.FOUND);
    }

    @ApiOperation(value = "Método de registro de préstamos", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Préstamo registrado"), @ApiResponse(code = 400, message = " "),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de registrar el préstamo." +
                    " Inténtelo mas tarde")})
    @PostMapping(value = "/prestamos", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO', 'ROLE_USUARIO')")
    public ResponseEntity<?> crearPrestamo(@Valid @RequestBody PrestamoDTO prestamoDTO, BindingResult result,
                                           Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        Prestamo prestamo = new Prestamo();
        Usuario usuarioPrestamo;
        Usuario empleadoPrestamo;
        Libro libroPrestamo = libroService.findById(prestamoDTO.getIdLibro()).orElseThrow();

        try {

            if (result.hasErrors()) {
                List<String> errores = result.getFieldErrors()
                        .stream()
                        .map(error -> error.getField() + " : " + error.getDefaultMessage())
                        .collect(Collectors.toList());
                response.put("mensaje", errores);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // SI ES USUARIO, REGISTRA EL PRÉSTAMO CON SU NOMBRE, EL LOCAL DEPENDE DEL
            // LIBRO ESCOGIDO Y EL EMPLEADO, MOMENTANEAMENTE, ES "PRUEBA" (ID 1)
            if (usuarioLogueado.getRol().getAuthority().equals("ROLE_USUARIO")) {
                /*if (!String.valueOf(prestamoDTO.getUsuario()).matches("^\\d+$") &&
                        !String.valueOf(prestamoDTO.getEmpleado()).matches("^\\d+$")) {*/
                empleadoPrestamo = usuarioService.findById(1L).orElseThrow();
                prestamoDTO.setObservaciones("Préstamo pendiente creado por un usuario, en espera de " +
                        "confirmación");
                // TODO: REVISAR
                // prestamo.setEmpleado(empleadoPrestamo);

                //prestamo.setLibro(libroPrestamo);
                /*} else {
                    response.put("mensaje", "Lo sentimos, hubo un error a la hora de registrar el préstamo");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }*/
                // SI NO ES USUARIO, VALIDO LOS DEMÁS ROLES
            } else {
                usuarioPrestamo = usuarioService.findById(prestamoDTO.getIdUsuario()).orElseThrow();
                empleadoPrestamo = usuarioService.findById(prestamoDTO.getIdEmpleado()).orElseThrow();

                if (libroPrestamo.isActivo() && empleadoPrestamo.isActivo()
                        && usuarioPrestamo.isActivo()) {
                    switch (usuarioLogueado.getRol().getAuthority()) {
                        case "ROLE_SYSADMIN":
                            // SI ES SYSADMIN, SETEA LOS CAMPOS DE EMPLEADO Y LIBRO QUE TENGAN EL MISMO LOCAL
                            /* TODO: REVISAR
                            if (!empleadoPrestamo.getLocal().getId()
                                    .equals(libroPrestamo.getLocal().getId())) {
                                response.put("mensaje",
                                        "Lo sentimos, hubo un error a la hora de registrar el préstamo. " +
                                                "El empleado y el libro no pertenecen al mismo local");
                                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                            }*/
                            prestamoDTO.setObservaciones("Préstamo creado y confirmado por el Sysadmin");
                            break;
                        case "ROLE_ADMIN":
                            // SI ES ADMIN, REGISTRO EL PRÉSTAMO POR LOCAL (SOLO LOS EMPLEADOS
                            // PERTENECIENTES AL LOCAL DONDE TRABAJA EL ADMIN, ASI COMO EL LOCAL Y EL LIBRO)
                            /* TODO: REVISAR
                            if (!empleadoPrestamo.getLocal().getId()
                                    .equals(libroPrestamo.getLocal().getId())) {
                                response.put("mensaje",
                                        "Lo sentimos, hubo un error a la hora de registrar el préstamo. " +
                                                "El empleado y el libro no pertenecen al mismo local");
                                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                            }*/
                            prestamoDTO.setObservaciones("Préstamo creado y confirmado por un Admin");
                            break;
                        case "ROLE_EMPLEADO":
                            // SI ES EMPLEADO, REGISTRA UN PRÉSTAMO SIMILAR AL ADMIN, PERO SETEA EL
                            // EMPLEADO CON SU NOMBRE
                            /* TODO: REVISAR
                            if (!empleadoPrestamo.getLocal().getId()
                                    .equals(libroPrestamo.getLocal().getId())) {
                                response.put("mensaje",
                                        "Lo sentimos, hubo un error a la hora de registrar el préstamo. " +
                                                "El empleado y el libro no pertenecen al mismo local");
                                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                            }*/
                            prestamoDTO.setObservaciones("Préstamo creado y confirmado por un empleado");
                            prestamoDTO.setIdEmpleado(usuarioLogueado.getId());
                            break;
                    }

                    // AQUÍ ACTUALIZO EL STOCK DEL LIBRO PRESTADO
                    if (libroPrestamo.getStock() <= 0) {
                        response.put("mensaje", "Lo sentimos, hubo un error a la hora de registrar el préstamo. " +
                                "No hay stock suficiente del libro seleccionado");
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    } else {
                        libroPrestamo.setStock(libroPrestamo.getStock() - 1);
                        libroService.save(libroPrestamo);
                        prestamoDTO.setIdLibro(libroPrestamo.getId());
                    }

                } else {
                    response.put("mensaje",
                            "Lo sentimos, hubo un error a la hora de registrar el préstamo." +
                                    " Verificar si el usuario, empleado y/o libro estén disponibles");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
                // TODO: REVISAR
                // prestamo.setUsuario(usuarioPrestamo);
            }

            prestamo.setFechaDevolucion(prestamoDTO.getFechaDevolucion());
            prestamo.setLibro(libroPrestamo);
            prestamo.setObservaciones(prestamoDTO.getObservaciones());
            // TODO: REVISAR
            // prestamo.setEmpleado(empleadoPrestamo);
            prestamoService.save(prestamo);

            if (usuarioLogueado.getRol().getAuthority().equals("ROLE_USUARIO")) {
                Map<String, Object> model = new HashMap<>();
                model.put("titulo", "Libro Solicitado");
                model.put("from", "Biblioteca2020 " + "<" + emailFrom + ">");
                // TODO: REVISAR
                // model.put("to", usuarioLogueado.getEmail());
                model.put("prestamo", prestamo);
                model.put("subject", "Libro Solicitado | Biblioteca2020");
                emailService.enviarEmail(model);
            }

        } catch (MessagingException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de enviar el correo de confirmación");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de registrar el préstamo");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "Préstamo registrado");
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
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    public ResponseEntity<?> confirmarPrestamo(@PathVariable String id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).orElseThrow();
        Optional<Prestamo> prestamoEncontrado;
        Map<String, Object> response = new HashMap<>();

        try {

            if (id.matches("^\\d+$")) {
                prestamoEncontrado = prestamoService.findById(Long.parseLong(id));
            } else {
                response.put("mensaje", "El id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            if (prestamoEncontrado.isPresent()) {
                switch (usuarioLogueado.getRol().getAuthority()) {
                    case "ROLE_SYSADMIN":
                        // SI ES SYSADMIN, CONFIRMO EL PRÉSTAMO CON SU ID EMPLEADO
                        prestamoEncontrado.get().setObservaciones(
                                "Orden de préstamo confirmada por el Sysadmin " + usuarioLogueado.getUsuario());
                        break;
                    case "ROLE_ADMIN":
                        // SI ES ADMIN, CONFIRMO EL PRÉSTAMO CON SU ID EMPLEADO
                        /* TODO: REVISAR
                        prestamoEncontrado.get().setObservaciones(
                                "Orden de préstamo confirmada por el Admin " + usuarioLogueado.getUsuario()
                                        + ", del local en " + usuarioLogueado.getLocal().getDireccion());*/
                        break;
                    case "ROLE_EMPLEADO":
                        // SI ES EMPLEADO, CONFIRMO EL PRÉSTAMO CON SU ID EMPLEADO
                        /* TODO: REVISAR
                        prestamoEncontrado.get().setObservaciones(
                                "Orden de préstamo confirmada por el Empleado " + usuarioLogueado.getUsuario()
                                        + ", del local en " + usuarioLogueado.getLocal().getDireccion());*/
                        break;
                }

                /* TODO: REVISAR
                if (prestamoEncontrado.get().getEmpleado().getRol().getAuthority().equals("ROLE_PRUEBA")) {
                    prestamoEncontrado.get().setEmpleado(usuarioLogueado);
                } else {
                    response.put("mensaje", "Lo sentimos, el préstamo ya está asociado a otro empleado");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }*/

                prestamoService.save(prestamoEncontrado.get());
                response.put("mensaje", "Préstamo confirmado");

                Map<String, Object> model = new HashMap<>();
                model.put("titulo", "Orden Confirmada");
                model.put("from", "Biblioteca2020 " + "<" + emailFrom + ">");
                // TODO: REVISAR
                // model.put("to", prestamoEncontrado.get().getUsuario().getEmail());
                model.put("prestamo", prestamoEncontrado.get());
                model.put("subject", "Orden Confirmada | Biblioteca2020");
                emailService.enviarEmail(model);

            } else {
                response.put("mensaje", "El préstamo no existe");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

        } catch (NoSuchElementException | DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de confirmar el préstamo");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (MessagingException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de enviar el correo de confirmación");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

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
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    public ResponseEntity<?> devolverLibro(@PathVariable String id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).orElseThrow();
        Optional<Prestamo> prestamoEncontrado;
        Libro libro;
        Map<String, Object> response = new HashMap<>();

        try {
            if (id.matches("^\\d+$")) {
                prestamoEncontrado = prestamoService.findById(Long.parseLong(id));
            } else {
                response.put("mensaje", "El id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            if (prestamoEncontrado.isPresent()) {
                switch (usuarioLogueado.getRol().getAuthority()) {
                    case "ROLE_SYSADMIN":
                        // SI ES SYSADMIN, DEVUELVO EL LIBRO CON SU ID EMPLEADO
                        prestamoEncontrado.get().setObservaciones("Libro " +
                                prestamoEncontrado.get().getLibro().getTitulo() +
                                " devuelto por el Sysadmin " + usuarioLogueado.getUsuario());
                        break;
                    case "ROLE_ADMIN":
                        // SI ES ADMIN, DEVUELVO EL LIBRO CON SU ID EMPLEADO
                        /* TODO: REVISAR
                        prestamoEncontrado.get().setObservaciones("Libro " +
                                prestamoEncontrado.get().getLibro().getTitulo() +
                                " devuelto por el Admin " + usuarioLogueado.getUsuario() +
                                ", del local en " + usuarioLogueado.getLocal().getDireccion());*/
                        break;
                    case "ROLE_EMPLEADO":
                        // SI ES EMPLEADO, DEVUELVO EL LIBRO CON SU ID EMPLEADO
                        /* TODO: REVISAR
                        prestamoEncontrado.get().setObservaciones("Libro " +
                                prestamoEncontrado.get().getLibro().getTitulo() +
                                " devuelto por el Empleado " + usuarioLogueado.getUsuario() +
                                ", del local en " + usuarioLogueado.getLocal().getDireccion());*/
                        break;
                }
                libro = libroService.findById(prestamoEncontrado.get().getLibro().getId()).orElseThrow();

                /* TODO: REVISAR
                if (prestamoEncontrado.get().isActivo()) {
                    response.put("mensaje", "El libro ya ha sido devuelto o anulado");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                } else {
                    libro.setStock(libro.getStock() + 1);
                    prestamoEncontrado.get().setActivo(true);
                }*/

                libroService.save(libro);

                prestamoService.save(prestamoEncontrado.get());
                response.put("mensaje", "Libro devuelto");
            } else {
                response.put("mensaje", "El préstamo no existe");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

        } catch (NoSuchElementException | DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de devolver el libro");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

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
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO', 'ROLE_USUARIO')")
    public ResponseEntity<?> anularPrestamo(@PathVariable String id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).orElseThrow();
        Usuario empleado;
        Optional<Prestamo> prestamoEncontrado;
        Libro libro;
        Map<String, Object> response = new HashMap<>();

        try {

            if (id.matches("^\\d+$")) {
                prestamoEncontrado = prestamoService.findById(Long.parseLong(id));
            } else {
                response.put("mensaje", "Lo sentimos, el id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            if (prestamoEncontrado.isPresent()) {
                switch (usuarioLogueado.getRol().getAuthority()) {
                    case "ROLE_SYSADMIN":
                        // SI ES SYSADMIN, ANULO EL PRÈSTAMO CON SU ID EMPLEADO
                        prestamoEncontrado.get()
                                .setObservaciones("El prèstamo del libro " +
                                        prestamoEncontrado.get().getLibro().getTitulo() +
                                        " ha sido anulado por el Sysadmin " + usuarioLogueado.getUsuario());
                        break;
                    case "ROLE_ADMIN":
                        // SI ES ADMIN, ANULO EL PRÈSTAMO CON SU ID EMPLEADO
                        /* TODO: REVISAR
                        prestamoEncontrado.get()
                                .setObservaciones("El prèstamo del libro " +
                                        prestamoEncontrado.get().getLibro().getTitulo()
                                        + " ha sido anulado por el Admin " + usuarioLogueado.getUsuario()
                                        + ", del local en " + usuarioLogueado.getLocal().getDireccion());*/
                        break;
                    case "ROLE_EMPLEADO":
                        // SI ES EMPLEADO, ANULO EL PRÈSTAMO CON SU ID EMPLEADO
                        /* TODO: REVISAR
                        prestamoEncontrado.get()
                                .setObservaciones("El prèstamo del libro " +
                                        prestamoEncontrado.get().getLibro().getTitulo() +
                                        " ha sido anulado por el Empleado " + usuarioLogueado.getUsuario() +
                                        ", del local en " + usuarioLogueado.getLocal().getDireccion());*/
                        break;
                    case "ROLE_USUARIO":
                        // SI ES USUARIO, ANULO EL PRÈSTAMO CON EL ID DE PRUEBA
                        // SOLO Y SOLO SI EL PRÉSTAMO A ANULAR ES EL MIO Y DE NADIE MAS
                        /* TODO: REVISAR
                         if (prestamoEncontrado.get().getUsuario().getId().equals(usuarioLogueado.getId())) {
                            empleado = usuarioService.findById(1L).orElseThrow();
                            prestamoEncontrado.get().setEmpleado(empleado);
                            prestamoEncontrado.get()
                                    .setObservaciones("El prèstamo del libro " +
                                            prestamoEncontrado.get().getLibro().getTitulo()
                                            + " ha sido anulado por el Usuario " + usuarioLogueado.getUsuario());
                        } else {
                            response.put("mensaje", "Lo sentimos, no tienes acceso a este recurso");
                            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                        }*/
                        break;
                }

                libro = libroService.findById(prestamoEncontrado.get().getLibro().getId()).orElseThrow();

                /* TODO: REVISAR
                if (prestamoEncontrado.get().isActivo()) {
                    response.put("mensaje", "El libro ya ha sido anulado o devuelto");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                } else {
                    libro.setStock(libro.getStock() + 1);
                    prestamoEncontrado.get().setActivo(true);
                }*/

                libroService.save(libro);
                prestamoService.save(prestamoEncontrado.get());
                response.put("mensaje", "Prèstamo anulado");

            } else {
                response.put("mensaje", "El préstamo no existe");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

        } catch (NoSuchElementException | DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de anular el prèstamo");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
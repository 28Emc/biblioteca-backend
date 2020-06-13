package com.biblioteca.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.mail.MessagingException;
import com.biblioteca.backend.model.Libro;
import com.biblioteca.backend.model.Prestamo;
import com.biblioteca.backend.model.Usuario;
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

@CrossOrigin(origins = { "*", "http://localhost:4200" })
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
        Libro libro = null;
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
                        response.put("mensaje", "Préstamo registrado correctamente!");
                        break;
                }
                // ANTES DE GUARDAR EL PRÉSTAMO, ACTUALIZAR EL STOCK DEL LIBRO PRESTADO
                libro = libroService.findById(prestamo.getLibro().getId()).get();
                if (libro.getStock() <= 0) {
                    response.put("mensaje", "Lo sentimos, hubo un error a la hora de registrar el préstamo!");
                    response.put("error", "no hay stock suficiente del libro seleccionado (" + libro.getTitulo() + ")");
                    return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
                } else {
                    // PERSISTIENDO LOS DATOS DEL "HIJO" LIBRO
                    libro.setStock(libro.getStock() - 1);
                    libroService.save(libro);
                }
                prestamoService.save(prestamo);
                // ENVÍO EL CORREO SOLAMENTE DESPUÉS DE HABER GUARDADO EN LA BBDD, Y SOLAMENTE
                // SI SOY USUARIO
                if (usuarioLogueado.getRol().getAuthority().equals("ROLE_USUARIO")) {
                    Map<String, Object> model = new HashMap<>();
                    model.put("titulo", "Libro Solicitado");
                    model.put("from", "Biblioteca2020 " + "<" + emailFrom + ">");
                    model.put("to", usuarioLogueado.getEmail());
                    // model.put("to", "edmech25@gmail.com");
                    model.put("prestamo", prestamo);
                    model.put("subject", "Libro Solicitado | Biblioteca2020");
                    emailService.enviarEmail(model);
                }
            } else {
                response.put("mensaje", "Lo sentimos, hubo un error a la hora de registrar el préstamo!");
                response.put("error", "Verificar si el usuario, el empleado y/o el libro estén disponibles");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de registrar el préstamo!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (MessagingException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de enviar el correo de confirmación!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de confirmación de préstamo generado por el usuario", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Préstamo confirmado"), @ApiResponse(code = 400, message = " "),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de confirmar el préstamo. Inténtelo mas tarde") })
    @PutMapping(value = "/prestamos/{id}/confirmar-prestamo", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    public ResponseEntity<?> confirmarPrestamo(@PathVariable Long id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByEmail(userDetails.getUsername()).get();
        Prestamo prestamoEncontrado = null;
        Map<String, Object> response = new HashMap<>();
        try {
            // VALIDAR PRÉSTAMO POR SU ID
            prestamoEncontrado = prestamoService.findById(id).get();
            if (prestamoEncontrado != null || id == null) {
                // VALIDAR QUE ROL TIENE EL USUARIO
                switch (usuarioLogueado.getRol().getAuthority()) {
                    case "ROLE_SYSADMIN":
                        // SI ES SYSADMIN, CONFIRMO EL PRÉSTAMO CON SU ID EMPLEADO
                        prestamoEncontrado.setObservaciones(
                                "Orden de préstamo confirmada por el Sysadmin " + usuarioLogueado.getUsuario());
                        break;
                    case "ROLE_ADMIN":
                        // SI ES ADMIN, CONFIRMO EL PRÉSTAMO CON SU ID EMPLEADO
                        prestamoEncontrado.setObservaciones(
                                "Orden de préstamo confirmada por el Admin " + usuarioLogueado.getUsuario()
                                        + ", del local en " + usuarioLogueado.getLocal().getDireccion());
                        break;
                    case "ROLE_EMPLEADO":
                        // SI ES EMPLEADO, CONFIRMO EL PRÉSTAMO CON SU ID EMPLEADO
                        prestamoEncontrado.setObservaciones(
                                "Orden de préstamo confirmada por el Empleado " + usuarioLogueado.getUsuario()
                                        + ", del local en " + usuarioLogueado.getLocal().getDireccion());
                        break;
                }
                prestamoEncontrado.setEmpleado(usuarioLogueado);
                prestamoService.save(prestamoEncontrado);
                response.put("mensaje", "Préstamo confirmado!");
                // ENVÍO EL CORREO SOLAMENTE DESPUÉS DE HABER GUARDADO EN LA BBDD, Y SOLAMENTE
                // SI SOY USUARIO
                Map<String, Object> model = new HashMap<>();
                model.put("titulo", "Orden Confirmada");
                model.put("from", "Biblioteca2020 " + "<" + emailFrom + ">");
                // model.put("to", prestamoEncontrado.getUsuario().getEmail());
                model.put("to", "edmech25@gmail.com");
                model.put("prestamo", prestamoEncontrado);
                model.put("subject", "Orden Confirmada | Biblioteca2020");
                emailService.enviarEmail(model);
            } else {
                response.put("mensaje", "Lo sentimos, hubo un error a la hora de confirmar el préstamo!");
                response.put("error", "El préstamo no existe en la BBDD.");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (DataIntegrityViolationException | NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de confirmar el préstamo!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (MessagingException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de enviar el correo de confirmación!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de devolución de libro", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Libro devuelto"), @ApiResponse(code = 400, message = " "),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de devolver el libro. Inténtelo mas tarde") })
    @PutMapping(value = "/prestamos/{id}/devolver-libro", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    public ResponseEntity<?> devolverLibro(@PathVariable Long id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByEmail(userDetails.getUsername()).get();
        Prestamo prestamoEncontrado = null;
        Libro libro = null;
        Map<String, Object> response = new HashMap<>();
        try {
            // VALIDAR PRÉSTAMO POR SU ID
            prestamoEncontrado = prestamoService.findById(id).get();
            if (prestamoEncontrado != null || id == null) {
                // VALIDAR QUE ROL TIENE EL USUARIO
                switch (usuarioLogueado.getRol().getAuthority()) {
                    case "ROLE_SYSADMIN":
                        // SI ES SYSADMIN, DEVUELVO EL LIBRO CON SU ID EMPLEADO
                        prestamoEncontrado.setObservaciones("Libro " + prestamoEncontrado.getLibro().getTitulo()
                                + " devuelto por el Sysadmin " + usuarioLogueado.getUsuario());
                        break;
                    case "ROLE_ADMIN":
                        // SI ES ADMIN, DEVUELVO EL LIBRO CON SU ID EMPLEADO
                        prestamoEncontrado.setObservaciones("Libro " + prestamoEncontrado.getLibro().getTitulo()
                                + " devuelto por el Admin " + usuarioLogueado.getUsuario() + ", del local en "
                                + usuarioLogueado.getLocal().getDireccion());
                        break;
                    case "ROLE_EMPLEADO":
                        // SI ES EMPLEADO, DEVUELVO EL LIBRO CON SU ID EMPLEADO
                        prestamoEncontrado.setObservaciones("Libro " + prestamoEncontrado.getLibro().getTitulo()
                                + " devuelto por el Empleado " + usuarioLogueado.getUsuario() + ", del local en "
                                + usuarioLogueado.getLocal().getDireccion());
                        break;
                }
                libro = libroService.findById(prestamoEncontrado.getLibro().getId()).get();
                // PERSISTIENDO LOS DATOS DEL "HIJO" LIBRO
                // ANTES DE ACTUALIZAR, PREGUNTO SI EL ESTADO DEL PRÈSTAMO ES VERDADERO
                // (PARA NO DEVOLVER 2 VECES EL MISMO LIBRO DEL MISMO PRÈSTAMO)
                if (prestamoEncontrado.isActivo()) {
                    response.put("mensaje", "Lo sentimos, hubo un error a la hora de devolver el libro!");
                    response.put("error", "El libro ya ha sido devuelto o anulado!.");
                    return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
                } else {
                    libro.setStock(libro.getStock() + 1);
                    prestamoEncontrado.setActivo(true);
                }
                libroService.save(libro);
                prestamoService.save(prestamoEncontrado);
                response.put("mensaje", "Libro devuelto!");
            } else {
                response.put("mensaje", "Lo sentimos, hubo un error a la hora de devolver el libro!");
                response.put("error", "El préstamo no existe en la BBDD.");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (DataIntegrityViolationException | NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de devolver el libro!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de anulaciòn de prèstamo de libro", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Prèstamo anulado"), @ApiResponse(code = 400, message = " "),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de anular el prèstamo. Inténtelo mas tarde") })
    @PutMapping(value = "/prestamos/{id}/anular-prestamo", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO', 'ROLE_USUARIO')")
    public ResponseEntity<?> anularPrestamo(@PathVariable Long id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByEmail(userDetails.getUsername()).get();
        Usuario empleado = null;
        Prestamo prestamoEncontrado = null;
        Libro libro = null;
        Map<String, Object> response = new HashMap<>();
        try {
            // VALIDAR PRÉSTAMO POR SU ID
            prestamoEncontrado = prestamoService.findById(id).get();
            if (prestamoEncontrado != null || id == null) {
                // VALIDAR QUE ROL TIENE EL USUARIO
                switch (usuarioLogueado.getRol().getAuthority()) {
                    case "ROLE_SYSADMIN":
                        // SI ES SYSADMIN, ANULO EL PRÈSTAMO CON SU ID EMPLEADO
                        prestamoEncontrado
                                .setObservaciones("El prèstamo del libro " + prestamoEncontrado.getLibro().getTitulo()
                                        + " ha sido anulado por el Sysadmin " + usuarioLogueado.getUsuario());
                        break;
                    case "ROLE_ADMIN":
                        // SI ES ADMIN, ANULO EL PRÈSTAMO CON SU ID EMPLEADO
                        prestamoEncontrado
                                .setObservaciones("El prèstamo del libro " + prestamoEncontrado.getLibro().getTitulo()
                                        + " ha sido anulado por el Admin " + usuarioLogueado.getUsuario()
                                        + ", del local en " + usuarioLogueado.getLocal().getDireccion());
                        break;
                    case "ROLE_EMPLEADO":
                        // SI ES EMPLEADO, ANULO EL PRÈSTAMO CON SU ID EMPLEADO
                        prestamoEncontrado
                                .setObservaciones("El prèstamo del libro " + prestamoEncontrado.getLibro().getTitulo()
                                        + " ha sido anulado por el Empleado " + usuarioLogueado.getUsuario()
                                        + ", del local en " + usuarioLogueado.getLocal().getDireccion());
                        break;
                    default:
                        // SI ES USUARIO, ANULO EL PRÈSTAMO CON EL ID DE PRUEBA
                        empleado = usuarioService.findById(1L).get();
                        prestamoEncontrado.setEmpleado(empleado);
                        prestamoEncontrado
                                .setObservaciones("El prèstamo del libro " + prestamoEncontrado.getLibro().getTitulo()
                                        + " ha sido anulado por el Usuario " + usuarioLogueado.getUsuario() + ".");
                        break;
                }
                libro = libroService.findById(prestamoEncontrado.getLibro().getId()).get();
                // PERSISTIENDO LOS DATOS DEL "HIJO" LIBRO
                // ANTES DE ACTUALIZAR, PREGUNTO SI EL ESTADO DEL PRÈSTAMO ES VERDADERO
                // (PARA NO DEVOLVER 2 VECES EL MISMO LIBRO DEL MISMO PRÈSTAMO)
                if (prestamoEncontrado.isActivo()) {
                    response.put("mensaje", "Lo sentimos, hubo un error a la hora de anular el prèstamo!");
                    response.put("error", "El libro ya ha sido anulado o devuelto!.");
                    return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
                } else {
                    libro.setStock(libro.getStock() + 1);
                    prestamoEncontrado.setActivo(true);
                }
                libroService.save(libro);
                prestamoService.save(prestamoEncontrado);
                response.put("mensaje", "Prèstamo anulado!");
            } else {
                response.put("mensaje", "Lo sentimos, hubo un error a la hora de anular el prèstamo!");
                response.put("error", "El préstamo no existe en la BBDD.");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (DataIntegrityViolationException | NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de anular el prèstamo!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }
}
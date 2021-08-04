package com.biblioteca.backend.controller;

import com.biblioteca.backend.model.Persona.DTO.PersonaDTO;
import com.biblioteca.backend.model.Token.Token;
import com.biblioteca.backend.model.Usuario.DTO.AccountRecovery;
import com.biblioteca.backend.model.Usuario.DTO.ChangePassword;
import com.biblioteca.backend.model.Usuario.Usuario;
import com.biblioteca.backend.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"*", "http://localhost:4200"})
@RestController
@Api(value = "usuario", description = "Operaciones referentes a los usuarios")
public class UsuarioController {

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private ILocalService localService;

    @Autowired
    private ITokenService tokenService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Value("{spring.mail.username}")
    private String emailFrom;

    @ApiOperation(value = "Método de listado de usuarios", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Usuarios encontrados"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar los usuarios. " +
                    "Inténtelo mas tarde")})
    @GetMapping(value = "/usuarios", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> listarUsuarios() {
        Map<String, Object> response = new HashMap<>();
        List<PersonaDTO> usuarios;

        try {
            usuarios = usuarioService.findAll();
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de buscar los usuarios");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.FOUND);
        }

        response.put("message", "Usuarios encontrados : ".concat(String.valueOf(usuarios.size())));
        response.put("data", usuarios);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de consulta de usuario por su id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Usuario encontrado"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar el usuario. " +
                    "Inténtelo mas tarde")})
    @GetMapping(value = "/usuarios/{id}", produces = "application/json")
    //@PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> buscarUsuario(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        PersonaDTO usuario;

        try {

            if (!id.matches("^\\d+$")) {
                response.put("message", "Lo sentimos, hubo un error a la hora de buscar el usuario");
                response.put("error", "El id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            usuario = usuarioService.findPersonaByIdUsuario(Long.valueOf(id));
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de buscar el usuario");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Usuario encontrado");
        response.put("data", usuario);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de registro de usuarios dentro del sistema", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Usuario registrado"),
            @ApiResponse(code = 400, message = "Lo sentimos, el correo ya està registrado con otro usuario"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de registrar el usuario. " +
                    "Inténtelo mas tarde")})
    @PostMapping(value = "/usuarios", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> crearUsuario(@Valid @RequestBody PersonaDTO personaDTO, BindingResult result) {
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

            usuarioService.saveAdmin(personaDTO);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de registrar el usuario");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Usuario registrado");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de actualización de usuarios", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Usuario actualizado"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "El usuario no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de actualizar el usuario." +
                    " Inténtelo mas tarde")})
    @PutMapping(value = {"/usuarios/{id}", "/perfil/{id}"}, produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    public ResponseEntity<?> editarUsuario(@Valid @RequestBody PersonaDTO personaDTO, BindingResult result,
                                           @PathVariable String id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();

        try {
            if (!id.matches("^\\d+$")) {
                response.put("message", "Lo sentimos, hubo un error a la hora de buscar el usuario");
                response.put("error", "El id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            if (result.hasErrors()) {
                List<String> errores = result.getFieldErrors()
                        .stream()
                        .map(error -> error.getField() + " : " + error.getDefaultMessage())
                        .collect(Collectors.toList());
                response.put("message", errores);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            usuarioService.update(Long.valueOf(id), personaDTO, usuarioLogueado);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de actualizar el usuario");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Usuario actualizado");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método para deshabilitar el usuario mediante el id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Usuario deshabilitado"),
            @ApiResponse(code = 201, message = " "), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "El usuario no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de deshabilitar el usuario." +
                    " Inténtelo mas tarde")})
    @PutMapping(value = "/usuarios/{id}/off", produces = "application/json")
    //@PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_USUARIO')")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> deshabilitarUsuario(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        try {

            if (!id.matches("^\\d+$")) {
                response.put("message", "Lo sentimos, hubo un error a la hora de buscar el usuario");
                response.put("error", "El id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            Usuario usuarioFound = usuarioService.changeUsuarioState(Long.valueOf(id), false);

            Map<String, Object> model = new HashMap<>();
            model.put("titulo", "Usuario Deshabilitado");
            model.put("from", "Biblioteca2020 " + "<" + emailFrom + ">");
            model.put("usuario", usuarioFound.getUsuario());
            model.put("to", usuarioFound.getUsuario());
            model.put("subject", "Usuario Deshabilitado | Biblioteca2020");
            emailService.enviarEmail(model);

        } catch (MessagingException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de enviar el correo de confirmación");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de deshabilitar el usuario");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Usuario deshabilitado");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método para habilitar un usuario mediante el id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Usuario habilitado"),
            @ApiResponse(code = 201, message = " "), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "El usuario no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de habilitar el usuario." +
                    " Inténtelo mas tarde")})
    @PutMapping(value = "/usuarios/{id}/on", produces = "application/json")
    //@PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN')")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> habilitarUsuario(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (!id.matches("^\\d+$")) {
                response.put("message", "Lo sentimos, hubo un error a la hora de buscar el usuario");
                response.put("error", "El id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            Usuario usuarioFound = usuarioService.changeUsuarioState(Long.valueOf(id), true);
        } catch (MessagingException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de enviar el correo de confirmación");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de habilitar el usuario");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Usuario habilitado");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de registro de usuarios (hecho para usuarios nuevos) con cuenta por activar",
            response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Usuario registrado"),
            @ApiResponse(code = 400, message = "Lo sentimos, el correo ya esta asociado con otro usuario"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de registrar el usuario." +
                    " Inténtelo mas tarde")})
    @PostMapping(value = "/cuenta", produces = "application/json")
    public ResponseEntity<?> crearCuentaNueva(@Valid @RequestBody PersonaDTO personaDTO, BindingResult result) {
        Map<String, Object> response = new HashMap<>();

        try {

            if (result.hasErrors()) {
                List<String> errores = result.getFieldErrors()
                        .stream()
                        .map(error -> error.getField() + " : " + error.getDefaultMessage())
                        .collect(Collectors.toList());
                response.put("message", "Lo sentimos, hubo un error a la hora de registrar el usuario");
                response.put("error", errores);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            Usuario usuarioNew = usuarioService.saveAdmin(personaDTO);
            Token tokenConfirma = usuarioService.createTokenAccount(usuarioNew, "OP1");

            // TODO: REEMPLAZAR POR URL DE SERVIDOR FINAL
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            Map<String, Object> model = new HashMap<>();
            model.put("titulo", "Validar Correo");
            model.put("enlace", baseUrl + "/cuenta/activar?token=" + tokenConfirma.getToken());
            model.put("from", "Biblioteca2020 " + "<" + emailFrom + ">");
            model.put("to", usuarioNew.getUsuario());
            model.put("subject", "Validar Correo | Biblioteca2020");
            emailService.enviarEmail(model);
        } catch (MessagingException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de enviar el correo de confirmación");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de registrar el usuario");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Registro completado. Se ha enviado un email de verificación para activar tu cuenta.");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de activación de cuenta de usuario mediante token", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Token validado y usuario activado." +
                    " Inicia sesión con sus nuevas credenciales"),
            @ApiResponse(code = 201, message = " "), @ApiResponse(code = 400, message = " "),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = "Lo sentimos, el enlace es inválido o el token ya caducó"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de procesar la solicitud." +
                    " Inténtelo mas tarde")})
    @RequestMapping(value = "/cuenta/activar", produces = "application/json", method = {RequestMethod.GET,
            RequestMethod.POST})
    public ResponseEntity<?> validarTokenActivacionCuentaNueva(@RequestParam("token") String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            usuarioService.activateUser(token);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de enviar la solicitud");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Activación de cuenta realizado con éxito." +
                " Inicia sesión con sus nuevas credenciales.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de recuperación de contraseña del usuario mediante email y DNI",
            response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Solicitud de recuperación de contraseña enviada"),
            @ApiResponse(code = 201, message = " "),
            @ApiResponse(code = 400, message = "Lo sentimos, su cuenta esta deshabilitada." +
                    " Ir a 'Activación de cuenta'"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = "El DNI y/o correo ingresados son incorrectos" +
                    " o el usuario no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de enviar la solicitud." +
                    " Inténtelo mas tarde")})
    @PostMapping(value = "/cuenta/recuperar-password", produces = "application/json")
    public ResponseEntity<?> enviarSolicitudRecuperacionContrasenaUsuario(
            @Valid @RequestBody AccountRecovery dtoAccountRecovery, BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        Usuario usuario;

        try {

            if (result.hasErrors()) {
                List<String> errores = result.getFieldErrors()
                        .stream()
                        .map(error -> error.getField() + " : " + error.getDefaultMessage())
                        .collect(Collectors.toList());
                response.put("message", "Lo sentimos, hubo un error a la hora de enviar la solicitud. Inténtelo mas tarde");
                response.put("error", errores);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            usuario = usuarioService.findByNroDocumentoAndUsuario(dtoAccountRecovery.getNroDocumento(),
                    dtoAccountRecovery.getEmail());
            Token tokenConfirma = usuarioService.createTokenAccount(usuario, "OP2");

            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            Map<String, Object> model = new HashMap<>();
            model.put("titulo", "Recuperar Password");
            model.put("enlace",
                    baseUrl + "/cuenta/recuperar-password/confirmar-token?token=" + tokenConfirma.getToken());
            model.put("from", "Biblioteca2020 " + "<" + emailFrom + ">");
            model.put("to", usuario.getUsuario());
            model.put("subject", "Recuperar Password | Biblioteca2020");
            emailService.enviarEmail(model);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de enviar la solicitud");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Solicitud de recuperación de contraseña enviada");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de validación de token de recuperación de contraseña de usuario",
            response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Token validado"),
            @ApiResponse(code = 201, message = " "), @ApiResponse(code = 400, message = " "),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = "El enlace es inválido o el token ya caducó"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de procesar la solicitud." +
                    " Inténtelo mas tarde")})
    @GetMapping(value = "/cuenta/recuperar-password/confirmar-token", produces = "application/json")
    public ResponseEntity<?> validarTokenRecuperacionContrasenaUsuario(@RequestParam("token") String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            response.put("changePassword", usuarioService.validateToken(token));
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de enviar la solicitud");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Token validado");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de cambio de contraseña del usuario (afuera del sistema)",
            response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Contraseña recuperada y actualizada"),
            @ApiResponse(code = 400, message = "Rellenar todos los campos"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de actualizar la contraseña." +
                    " Inténtelo mas tarde")})
    @PostMapping(value = "/cuenta/recuperar-password/confirmar-token", produces = "application/json")
    public ResponseEntity<?> recuperarContrasenaUsuario(@RequestBody ChangePassword dtoPassword) {
        Map<String, Object> response = new HashMap<>();
        Usuario usuarioRecuperado;

        try {
            usuarioRecuperado = usuarioService.recuperarPassword(dtoPassword);

            Map<String, Object> model = new HashMap<>();
            model.put("titulo", "Contraseña Actualizada");
            model.put("from", "Biblioteca2020 " + "<" + emailFrom + ">");
            model.put("usuario", usuarioRecuperado.getUsuario());
            model.put("to", usuarioRecuperado.getUsuario());
            model.put("subject", "Contraseña Actualizada | Biblioteca2020");
            emailService.enviarEmail(model);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de actualizar la contraseña");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Contraseña recuperada y actualizada");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de activación de cuenta de usuario mediante email y DNI",
            response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Solicitud de activación de cuenta enviada"),
            @ApiResponse(code = 201, message = " "),
            @ApiResponse(code = 400, message = "Estimado usuario, su cuenta se encuentra activa actualmente," +
                    " por lo tanto su solicitud no puede ser procesada"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = "El DNI y/o correo ingresados son incorrectos"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de enviar la solicitud." +
                    " Inténtelo mas tarde")})
    @PostMapping(value = "/cuenta/reactivar-cuenta", produces = "application/json")
    public ResponseEntity<?> enviarSolicitudReactivacionCuentaUsuario(
            @Valid @RequestBody AccountRecovery dtoAccountRecovery, BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        Usuario usuario;

        try {

            if (result.hasErrors()) {
                List<String> errores = result.getFieldErrors()
                        .stream()
                        .map(error -> error.getField() + " : " + error.getDefaultMessage())
                        .collect(Collectors.toList());
                response.put("message", "Lo sentimos, hubo un error a la hora de enviar la solicitud");
                response.put("error", errores);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            usuario = usuarioService.findByNroDocumentoAndUsuario(dtoAccountRecovery.getNroDocumento(),
                    dtoAccountRecovery.getEmail());
            Token tokenConfirma = usuarioService.createTokenAccount(usuario, "OP3");

            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            Map<String, Object> model = new HashMap<>();
            model.put("titulo", "Reactivacion Cuenta");
            model.put("enlace", baseUrl + "/cuenta/reactivar-cuenta/confirmar-token?token=" +
                    tokenConfirma.getToken());
            model.put("from", "Biblioteca2020 " + "<" + emailFrom + ">");
            model.put("usuario", usuario.getUsuario());
            model.put("to", usuario.getUsuario());
            model.put("subject", "Reactivacion Cuenta | Biblioteca2020");
            emailService.enviarEmail(model);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de enviar la solicitud");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Solicitud de activación de cuenta enviada");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de validación de token de activación de cuenta de usuario",
            response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Activacion de cuenta de usuario realizada de manera satisfactoria." +
                    " Ahora puede iniciar sesión."),
            @ApiResponse(code = 201, message = " "), @ApiResponse(code = 400, message = " "),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = "El enlace es inválido o el token ya caducó"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de procesar la solicitud." +
                    " Inténtelo mas tarde")})
    @GetMapping(value = "/cuenta/reactivar-cuenta/confirmar-token", produces = "application/json")
    public ResponseEntity<?> validarTokenRecuperacionCuentaUsuario(@RequestParam("token") String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            usuarioService.activateUser(token);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de enviar la solicitud");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Reactivacion de cuenta completada. Ahora puede iniciar sesión");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de cambio de contraseña del usuario logueado (dentro del sistema)",
            response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Contraseña actualizada"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de actualizar la contraseña." +
                    " Inténtelo mas tarde")})
    @PutMapping(value = "/usuarios/cambiar-password", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLEADO', 'ROLE_USUARIO')")
    public ResponseEntity<?> cambiarContrasenaUsuario(@RequestBody ChangePassword dtoPassword,
                                                      Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        try {
            usuarioService.cambiarPassword(dtoPassword, userDetails.getUsername());
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de actualizar la contraseña");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Contraseña actualizada");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
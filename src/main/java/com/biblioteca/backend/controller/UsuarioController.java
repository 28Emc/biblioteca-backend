package com.biblioteca.backend.controller;

import java.util.*;
import javax.mail.MessagingException;

import com.biblioteca.backend.model.Token;
import com.biblioteca.backend.model.Usuario.DTO.UsuarioDTO;
import com.biblioteca.backend.model.Usuario.Usuario;
import com.biblioteca.backend.model.Usuario.DTO.AccountRecovery;
import com.biblioteca.backend.model.Usuario.DTO.ChangePassword;
import com.biblioteca.backend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

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
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar los usuarios. Inténtelo mas tarde")})
    @GetMapping(value = "/usuarios", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    public ResponseEntity<?> listarUsuarios() {
        Map<String, Object> response = new HashMap<>();
        List<Usuario> usuarios;
        try {
            usuarios = usuarioService.findByRol("ROLE_USUARIO");
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de buscar los usuarios");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.FOUND);
        }
        response.put("usuarios", usuarios);
        response.put("mensaje", "Usuarios encontrados");
        return new ResponseEntity<>(response, HttpStatus.FOUND);
    }

    @ApiOperation(value = "Método de consulta de usuario por su id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Usuario encontrado"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar el usuario. Inténtelo mas tarde")})
    @GetMapping(value = "/usuarios/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    public ResponseEntity<?> buscarUsuario(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        Usuario usuario;
        try {
            if (id.matches("^\\d+$")) {
                usuario = usuarioService.findById(Long.parseLong(id)).orElseThrow();
            } else {
                response.put("mensaje", "Lo sentimos, el id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            if (usuario.getId() == 1 || !usuario.getRol().getAuthority().equals("ROLE_USUARIO")) {
                response.put("mensaje", "Lo sentimos, el usuario no existe");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, el usuario no existe");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de buscar el usuario");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("usuario", usuario);
        response.put("mensaje", "Usuario encontrado");
        return new ResponseEntity<>(response, HttpStatus.FOUND);
    }

    @ApiOperation(value = "Método de listado de empleados", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Empleados encontrados"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar los empleados. Inténtelo mas tarde")})
    @GetMapping(value = "/empleados", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<?> listarEmpleados(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario empleadoLogueado = usuarioService.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new NoSuchElementException("El empleado no existe")
        );
        List<Usuario> empleados = new ArrayList<>();
        try {
            switch (empleadoLogueado.getRol().getAuthority()) {
                case "ROLE_SYSADMIN":
                    empleados = usuarioService.findByRoles();
                    break;
                case "ROLE_ADMIN":
                    empleados = usuarioService.findByLocal(empleadoLogueado.getLocal().getId());
                    break;
            }
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de buscar los empleados");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.FOUND);
        }
        response.put("empleados", empleados);
        response.put("mensaje", "Empleados encontrados");
        return new ResponseEntity<>(response, HttpStatus.FOUND);
    }

    @ApiOperation(value = "Método de consulta de empleados por su id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Empleado encontrado"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar el empleado. Inténtelo mas tarde")})
    @GetMapping(value = "/empleados/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<?> buscarEmpleado(@PathVariable String id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario empleadoLogueado = usuarioService.findByEmail(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        Usuario empleado;
        try {
            if (id.matches("^\\d+$")) {
                empleado = usuarioService.findById(Long.parseLong(id)).orElseThrow();
            } else {
                response.put("mensaje", "Lo sentimos, el id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            // EL EMPLEADO CON ID 1 ES NECESARIO PARA LA LÓGICA DEL SISTEMA, ASI QUE NO PUEDE SER MODIFICADO
            if (empleado.getId() == 1 || empleado.getRol().getAuthority().equals("ROLE_USUARIO")) {
                response.put("mensaje", "Lo sentimos, el empleado no existe");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else if (empleadoLogueado.getRol().getAuthority().equals("ROLE_ADMIN")
                    && !empleado.getLocal().getId().equals(empleadoLogueado.getLocal().getId())) {
                // PENSAR SI COLOCAR QUE "EL EMPLEADO NO EXISTE" DIRECTAMENTE
                response.put("mensaje", "Lo sentimos, no puedes consultar empleados de otros locales");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, el empleado no existe");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de buscar el empleado");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("empleado", empleado);
        response.put("mensaje", "Empleado encontrado");
        return new ResponseEntity<>(response, HttpStatus.FOUND);
    }

    @ApiOperation(value = "Método de registro de usuarios (hecho para usuarios nuevos) con cuenta por activar", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Usuario registrado"),
            @ApiResponse(code = 400, message = "Lo sentimos, el correo ya està asociado con otro usuario"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de registrar el usuario. Inténtelo mas tarde")})
    @PostMapping(value = "/cuenta", produces = "application/json")
    public ResponseEntity<?> crearCuentaNueva(@RequestBody UsuarioDTO usuarioDTO) {
        Map<String, Object> response = new HashMap<>();
        Optional<Usuario> usuarioEncontrado;
        try {
            // TODO: AQUI VA LA VALIDACIÓN DEL OBJETO (@NOTBLANK, @VALID)
            usuarioEncontrado = usuarioService.findByEmail(usuarioDTO.getEmail());
            if (usuarioEncontrado.isPresent()) {
                response.put("mensaje", "Lo sentimos, el correo ya està asociado con otro usuario");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else {
                Usuario usuario = new Usuario();
                // VALIDACION DE NOMBRE Y DEMAS CAMPOS AQUI
                usuario.setNombres(usuarioDTO.getNombres());
                usuario.setApellidoPaterno(usuarioDTO.getApellidoPaterno());
                usuario.setApellidoMaterno(usuarioDTO.getApellidoMaterno());
                usuario.setDni(usuarioDTO.getDni());
                usuario.setDireccion(usuarioDTO.getDireccion());
                usuario.setCelular(usuarioDTO.getCelular());
                usuario.setEmail(usuarioDTO.getEmail());
                usuario.setUsuario(usuarioDTO.getUsuario());
                usuario.setPassword(encoder.encode(usuarioDTO.getPassword()));
                usuario.setFotoUsuario(usuarioDTO.getFotoUsuario());
                usuario.setActivo(false);

                usuario.setRol(roleService.findByAuthority("ROLE_USUARIO").orElseThrow());
                usuario.setLocal(localService.findById(1L).orElseThrow());

                usuarioService.save(usuario);
                Token tokenConfirma = new Token(usuario, "ACTIVAR CUENTA");
                tokenService.save(tokenConfirma);
                response.put("tokenValidacion", tokenConfirma.getToken());
                //}

                String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
                Map<String, Object> model = new HashMap<>();
                model.put("titulo", "Validar Correo");
                model.put("enlace", baseUrl + "/cuenta/activar?token=" + response.get("tokenValidacion"));
                model.put("from", "Biblioteca2020 " + "<" + emailFrom + ">");
                model.put("to", usuario.getEmail());
                model.put("subject", "Validar Correo | Biblioteca2020");
                emailService.enviarEmail(model);
            }
        } catch (NoSuchElementException | DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de registrar el usuario");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (MessagingException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de enviar el correo de confirmación");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Registro completado. Se ha enviado un email de verificación para activar tu cuenta");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de activación de cuenta de usuario mediante token", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Token validado y usuario activado. Inicia sesión con sus nuevas credenciales"),
            @ApiResponse(code = 201, message = " "), @ApiResponse(code = 400, message = " "),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = "Lo sentimos, el enlace es inválido o el token ya caducó"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de procesar la solicitud. Inténtelo mas tarde")})
    @RequestMapping(value = "/cuenta/activar", produces = "application/json", method = {RequestMethod.GET,
            RequestMethod.POST})
    public ResponseEntity<?> validarTokenActivacionCuentaNueva(@RequestParam("token") String token) {
        Map<String, Object> response = new HashMap<>();
        Token tokenConfirma;
        Usuario usuario;
        try {
            tokenConfirma = tokenService.findByToken(token).orElseThrow();
            usuario = usuarioService.findByEmail(tokenConfirma.getUsuario().getEmail()).orElseThrow();
            usuario.setActivo(true);
            usuarioService.save(usuario);
            tokenService.delete(tokenConfirma.getId());
        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, el enlace es inválido o el token ya caducó");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de enviar la solicitud. Inténtelo mas tarde");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Activación de cuenta realizado con éxito. Inicia sesión con sus nuevas credenciales");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de recuperación de contraseña del usuario mediante email y DNI", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Solicitud de recuperación de contraseña enviada"),
            @ApiResponse(code = 201, message = " "),
            @ApiResponse(code = 400, message = "Lo sentimos, su cuenta està deshabilitada. Ir a 'Reactivación de cuenta'"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = "Lo sentimos, el DNI y/o correo ingresados son incorrectos o el usuario no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de enviar la solicitud. Inténtelo mas tarde")})
    @PostMapping(value = "/cuenta/recuperar-password", produces = "application/json")
    public ResponseEntity<?> enviarSolicitudRecuperacionContrasenaUsuario(@RequestBody AccountRecovery dtoAccountRecovery) {
        Map<String, Object> response = new HashMap<>();
        Optional<Usuario> usuario;
        try {
            usuario = usuarioService.findByDniAndEmail(dtoAccountRecovery.getNroDocumento(),
                    dtoAccountRecovery.getEmail());
            if (usuario.isEmpty()) {
                response.put("mensaje",
                        "Lo sentimos, el DNI y/o correo ingresados son incorrectos o el usuario no existe");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else if (!usuario.get().isActivo()) {
                response.put("mensaje", "Lo sentimos, su cuenta està deshabilitada. Ir a 'Reactivación de cuenta'");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            Token tokenConfirma = new Token(usuario.get(), "RECUPERAR CONTRASEÑA");
            tokenService.save(tokenConfirma);
            response.put("tokenValidacion", tokenConfirma.getToken());

            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            Map<String, Object> model = new HashMap<>();
            model.put("titulo", "Recuperar Password");
            model.put("enlace",
                    baseUrl + "/cuenta/recuperar-password/confirmar-token?token=" + response.get("tokenValidacion"));
            model.put("from", "Biblioteca2020 " + "<" + emailFrom + ">");
            model.put("to", usuario.get().getEmail());
            model.put("subject", "Recuperar Password | Biblioteca2020");
            emailService.enviarEmail(model);

        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de enviar la solicitud. Inténtelo mas tarde");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Solicitud de recuperación de contraseña enviada");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de validación de token de recuperación de contraseña de usuario", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Token validado"),
            @ApiResponse(code = 201, message = " "), @ApiResponse(code = 400, message = " "),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = "Lo sentimos, el enlace es inválido o el token ya caducó"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de procesar la solicitud. Inténtelo mas tarde")})
    @GetMapping(value = "/cuenta/recuperar-password/confirmar-token", produces = "application/json")
    public ResponseEntity<?> validarTokenRecuperacionContrasenaUsuario(@RequestParam("token") String token) {
        Map<String, Object> response = new HashMap<>();
        ChangePassword dtoPassword;
        Token tokenConfirma;
        try {
            tokenConfirma = tokenService.findByToken(token).orElseThrow();
            dtoPassword = new ChangePassword();
            Usuario usuario = usuarioService.findByEmail(tokenConfirma.getUsuario().getEmail()).orElseThrow();
            dtoPassword.setId(usuario.getId());
            response.put("changePassword", dtoPassword);
            tokenService.delete(tokenConfirma.getId());
        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, el enlace es inválido o el token ya caducó");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de enviar la solicitud. Inténtelo mas tarde");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Token validado");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de cambio de contraseña del usuario (afuera del sistema)", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Contraseña recuperada y actualizada"),
            @ApiResponse(code = 400, message = "Rellenar todos los campos"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de actualizar la contraseña. Inténtelo mas tarde")})
    @PostMapping(value = "/cuenta/recuperar-password/confirmar-token", produces = "application/json")
    public ResponseEntity<?> recuperarContrasenaUsuario(@RequestBody ChangePassword dtoPassword) {
        Map<String, Object> response = new HashMap<>();
        Usuario usuarioNuevo;
        dtoPassword.setPasswordActual(null);
        try {
            if (dtoPassword.getNuevaPassword().equals("") || dtoPassword.getConfirmarPassword().equals("")) {
                response.put("mensaje", "Rellenar todos los campos!");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            usuarioNuevo = usuarioService.findById(dtoPassword.getId()).orElseThrow();
            usuarioService.recuperarPassword(dtoPassword);

            Map<String, Object> model = new HashMap<>();
            model.put("titulo", "Contraseña Actualizada");
            model.put("from", "Biblioteca2020 " + "<" + emailFrom + ">");
            model.put("usuario", usuarioNuevo.getUsuario());
            model.put("to", usuarioNuevo.getEmail());
            model.put("subject", "Contraseña Actualizada | Biblioteca2020");
            emailService.enviarEmail(model);

        } catch (NoSuchElementException | DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de actualizar la contraseña");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de actualizar la contraseña. Inténtelo mas tarde");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Contraseña recuperada y actualizada");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de reactivación de cuenta de usuario mediante email y DNI", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Solicitud de reactivación de cuenta enviada"),
            @ApiResponse(code = 201, message = " "),
            @ApiResponse(code = 400, message = "Estimado usuario, su cuenta se encuentra activa actualmente, por lo tanto su solicitud no puede ser procesada."),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = "Lo sentimos, el DNI y/o correo ingresados son incorrectos"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de enviar la solicitud. Inténtelo mas tarde")})
    @PostMapping(value = "/cuenta/reactivar-cuenta", produces = "application/json")
    public ResponseEntity<?> enviarSolicitudReactivacionCuentaUsuario(@RequestBody AccountRecovery dtoAccountRecovery) {
        Map<String, Object> response = new HashMap<>();
        Optional<Usuario> usuario;
        try {
            usuario = usuarioService.findByDniAndEmail(dtoAccountRecovery.getNroDocumento(),
                    dtoAccountRecovery.getEmail());
            if (usuario.isEmpty()) {
                response.put("mensaje", "Lo sentimos, el DNI y/o correo ingresados son incorrectos");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else if (usuario.get().isActivo()) {
                response.put("mensaje",
                        "Estimado usuario, su cuenta se encuentra actualmente activa, por lo tanto su solicitud no puede ser procesada");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            Token tokenConfirma = new Token(usuario.get(), "REACTIVAR CUENTA");
            tokenService.save(tokenConfirma);
            response.put("tokenValidacion", tokenConfirma.getToken());

            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            Map<String, Object> model = new HashMap<>();
            model.put("titulo", "Reactivacion Cuenta");
            model.put("enlace",
                    baseUrl + "/cuenta/reactivar-cuenta/confirmar-token?token=" + response.get("tokenValidacion"));
            model.put("from", "Biblioteca2020 " + "<" + emailFrom + ">");
            model.put("usuario", usuario.get().getUsuario());
            model.put("to", usuario.get().getEmail());
            model.put("subject", "Reactivacion Cuenta | Biblioteca2020");
            emailService.enviarEmail(model);

        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de enviar la solicitud");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de enviar la solicitud. Inténtelo mas tarde");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Solicitud de reactivación de cuenta enviada");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de validación de token de reactivación de cuenta de usuario", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Reactivacion de cuenta de usuario realizada de manera satisfactoria. Ahora puede iniciar sesión."),
            @ApiResponse(code = 201, message = " "), @ApiResponse(code = 400, message = " "),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = "Lo sentimos, el enlace es inválido o el token ya caducó"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de procesar la solicitud. Inténtelo mas tarde")})
    @GetMapping(value = "/cuenta/reactivar-cuenta/confirmar-token", produces = "application/json")
    public ResponseEntity<?> validarTokenRecuperacionCuentaUsuario(@RequestParam("token") String token) {
        Map<String, Object> response = new HashMap<>();
        Token tokenConfirma;
        try {
            tokenConfirma = tokenService.findByToken(token).orElseThrow();
            Usuario usuario = usuarioService.findByEmail(tokenConfirma.getUsuario().getEmail()).orElseThrow();
            usuario.setActivo(true);
            usuarioService.save(usuario);
            tokenService.delete(tokenConfirma.getId());
        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, el enlace es inválido o el token ya caducó");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de enviar la solicitud. Inténtelo mas tarde");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje",
                "Reactivacion de cuenta completada. Ahora puede iniciar sesión");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de registro de usuarios dentro del sistema", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Usuario registrado"),
            @ApiResponse(code = 400, message = "Lo sentimos, el correo ya està registrado con otro usuario"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de registrar el usuario. Inténtelo mas tarde")})
    @PostMapping(value = "/usuarios", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<?> crearUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        Map<String, Object> response = new HashMap<>();
        Optional<Usuario> usuarioEncontrado;
        try {
            usuarioEncontrado = usuarioService.findByEmail(usuarioDTO.getEmail());
            if (usuarioEncontrado.isPresent()) {
                response.put("mensaje", "Lo sentimos, el correo ya està registrado");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else {
                Usuario usuario = new Usuario();
                usuario.setNombres(usuarioDTO.getNombres());
                usuario.setApellidoPaterno(usuarioDTO.getApellidoPaterno());
                usuario.setApellidoMaterno(usuarioDTO.getApellidoMaterno());
                usuario.setDni(usuarioDTO.getDni());
                usuario.setDireccion(usuarioDTO.getDireccion());
                usuario.setCelular(usuarioDTO.getCelular());
                usuario.setEmail(usuarioDTO.getEmail());
                usuario.setUsuario(usuarioDTO.getUsuario());
                usuario.setPassword(encoder.encode(usuarioDTO.getPassword()));
                usuario.setFotoUsuario(usuarioDTO.getFotoUsuario());
                usuario.setActivo(true);
                /*
                    VALIDO QUE NO LE PASE NADA COMO PARAMETRO A ROL Y LOCAL
                    EN ESE CASO LE ASIGNO ROL_USUARIO Y LOCAL 1
                */
                // TODO: EN ANGULAR, CUANDO EL ROL SEA ROLE_USUARIO, ESCONDO EL LOCAL
                if (usuarioDTO.getRol() == null && usuarioDTO.getLocal() == null) {
                    usuario.setRol(roleService.findById(4L).orElseThrow());
                    usuario.setLocal(localService.findById(1L).orElseThrow());
                } else {
                    response.put("mensaje", "Lo sentimos, rol o local inválidos");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
                usuarioService.save(usuario);
            }
        } catch (NoSuchElementException | DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de registrar el usuario");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Usuario registrado");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de registro de empleados dentro del sistema", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Empleado registrado"),
            @ApiResponse(code = 400, message = "Lo sentimos, el correo ya està registrado con otro empleado"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de registrar el empleado. Inténtelo mas tarde")})
    @PostMapping(value = "/empleados", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<?> crearEmpleado(@RequestBody UsuarioDTO usuarioDTO) {
        Map<String, Object> response = new HashMap<>();
        Optional<Usuario> empleadoEncontrado;
        try {
            empleadoEncontrado = usuarioService.findByEmail(usuarioDTO.getEmail());
            if (empleadoEncontrado.isPresent()) {
                response.put("mensaje", "Lo sentimos, el correo ya està registrado");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else {
                Usuario empleado = new Usuario();
                empleado.setNombres(usuarioDTO.getNombres());
                empleado.setApellidoPaterno(usuarioDTO.getApellidoPaterno());
                empleado.setApellidoMaterno(usuarioDTO.getApellidoMaterno());
                empleado.setDni(usuarioDTO.getDni());
                empleado.setDireccion(usuarioDTO.getDireccion());
                empleado.setCelular(usuarioDTO.getCelular());
                empleado.setEmail(usuarioDTO.getEmail());
                empleado.setUsuario(usuarioDTO.getUsuario());
                empleado.setPassword(encoder.encode(usuarioDTO.getPassword()));
                empleado.setFotoUsuario(usuarioDTO.getFotoUsuario());
                empleado.setActivo(true);
                /*
                    DESPUES VALIDO EL ROLE_EMPLEADO O ROLE_ADMIN NO TENGAN EL LOCAL CON ID 1
                    PARA ASIGNARLE AL FINAL EL ROL Y EL LOCAL DESIGNADOS
                */
                if (!String.valueOf(usuarioDTO.getRol()).equals("4") && !String.valueOf(usuarioDTO.getLocal()).equals("1")) {
                    empleado.setRol(roleService.findById(usuarioDTO.getRol()).orElseThrow());
                    empleado.setLocal(localService.findById(usuarioDTO.getLocal()).orElseThrow());
                } else {
                    response.put("mensaje", "Lo sentimos, rol o local inválidos");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
                usuarioService.save(empleado);
            }
        } catch (NoSuchElementException | DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de registrar el empleado");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Empleado registrado");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de actualización de usuarios", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Usuario actualizado"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "El usuario no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de actualizar el usuario. Inténtelo mas tarde")})
    @PutMapping(value = "/usuarios/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    public ResponseEntity<?> editarUsuario(@RequestBody UsuarioDTO usuarioDTO, @PathVariable String id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByEmail(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        Usuario usuarioEncontrado;
        try {
            if (id.matches("^\\d+$")) {
                usuarioEncontrado = usuarioService.findById(Long.parseLong(id)).orElseThrow();
            } else {
                response.put("mensaje", "Lo sentimos, el id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            usuarioEncontrado.setNombres(usuarioDTO.getNombres()); // SOLO LECTURA?
            usuarioEncontrado.setApellidoMaterno(usuarioDTO.getApellidoMaterno()); // SOLO LECTURA?
            usuarioEncontrado.setApellidoPaterno(usuarioDTO.getApellidoPaterno()); // SOLO LECTURA?
            usuarioEncontrado.setDni(usuarioDTO.getDni()); // SOLO LECTURA?
            usuarioEncontrado.setDireccion(usuarioDTO.getDireccion());
            usuarioEncontrado.setCelular(usuarioDTO.getCelular()); // SOLO LECTURA?
            usuarioEncontrado.setEmail(usuarioDTO.getEmail()); // SOLO LECTURA?
            usuarioEncontrado.setFotoUsuario(usuarioDTO.getFotoUsuario());
            // SI ES MI REGISTRO, SETEO MIS PROPIOS CAMPOS EN EL ROL Y LOCAL
            if (usuarioEncontrado.getId().equals(usuarioLogueado.getId())) {
                usuarioEncontrado.setRol(usuarioLogueado.getRol());
                usuarioEncontrado.setLocal(usuarioLogueado.getLocal());
                /* TODO: PARA QUE SYSADMIN PUEDA HACER QUE UN USUARIO SEA
                    - EMPLEADO, VALIDO QUE LE PASE ROL CON ID 3 Y EL LOCAL NO TENGA ID 1
                    - USUARIO, VALIDO QUE LE PASE ROL CON ID 4 Y EL LOCAL TENGA SOLAMENTE ID 1
                    - ADMIN, VALIDO QUE EL ADMIN DEL LOCAL DESIGNADO NO EXISTA, Y EL LOCAL NO TENGA ID 1
                */
            } else if ("ROLE_EMPLEADO".equals(roleService.findById(usuarioDTO.getRol()).orElseThrow().getAuthority()) &&
                    localService.findById(usuarioDTO.getLocal()).orElseThrow().getId() != 1) {
                usuarioEncontrado.setRol(roleService.findById(usuarioDTO.getRol()).orElseThrow());
                usuarioEncontrado.setLocal(localService.findById(usuarioDTO.getLocal()).orElseThrow());
            } else if ("ROLE_USUARIO".equals(roleService.findById(usuarioDTO.getRol()).orElseThrow().getAuthority()) &&
                    localService.findById(usuarioDTO.getLocal()).orElseThrow().getId() == 1) {
                usuarioEncontrado.setRol(roleService.findById(usuarioDTO.getRol()).orElseThrow());
                usuarioEncontrado.setLocal(localService.findById(usuarioDTO.getLocal()).orElseThrow());
            } else if ("ROLE_ADMIN".equals(roleService.findById(usuarioDTO.getRol()).orElseThrow().getAuthority()) &&
                    usuarioService.existsAdminInLocal(usuarioDTO.getLocal()).isPresent() &&
                    localService.findById(usuarioDTO.getLocal()).orElseThrow().getId() != 1) {
                usuarioEncontrado.setRol(roleService.findById(usuarioDTO.getRol()).orElseThrow());
                usuarioEncontrado.setLocal(localService.findById(usuarioDTO.getLocal()).orElseThrow());
            } else {
                response.put("mensaje", "Lo sentimos, el rol y/o local asignados son inválidos o no están disponibles" +
                        " actualmente para este usuario");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            usuarioEncontrado.setUsuario(usuarioDTO.getUsuario()); // SOLO LECTURA?
            usuarioService.save(usuarioEncontrado);
        } catch (NoSuchElementException | DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de actualizar el usuario");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Usuario actualizado");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de actualización de empleados", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "empleado actualizado"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "El empleado no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de actualizar el empleado. Inténtelo mas tarde")})
    @PutMapping(value = "/empleados/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<?> editarEmpleado(@RequestBody UsuarioDTO usuarioDTO, @PathVariable String id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario empleadoLogueado = usuarioService.findByEmail(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        Usuario empleadoEncontrado;
        try {
            if (id.matches("^\\d+$")) {
                empleadoEncontrado = usuarioService.findById(Long.parseLong(id)).orElseThrow();
            } else {
                response.put("mensaje", "Lo sentimos, el id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            empleadoEncontrado.setNombres(usuarioDTO.getNombres()); // SOLO LECTURA?
            empleadoEncontrado.setApellidoMaterno(usuarioDTO.getApellidoMaterno()); // SOLO LECTURA?
            empleadoEncontrado.setApellidoPaterno(usuarioDTO.getApellidoPaterno()); // SOLO LECTURA?
            empleadoEncontrado.setDni(usuarioDTO.getDni()); // SOLO LECTURA?
            empleadoEncontrado.setDireccion(usuarioDTO.getDireccion());
            empleadoEncontrado.setCelular(usuarioDTO.getCelular()); // SOLO LECTURA?
            empleadoEncontrado.setEmail(usuarioDTO.getEmail()); // SOLO LECTURA?
            empleadoEncontrado.setFotoUsuario(usuarioDTO.getFotoUsuario()); // SOLO LECTURA?
            // VALIDO SI ESTOY ACTUALIZANDO MI PROPIO REGISTRO PRIMERO, PARA ESCONDER LOS ROLES Y LOCALES
            if (empleadoEncontrado.getId().equals(empleadoLogueado.getId())) {
                // TODO: EN ANGULAR ESCONDO EL ROL Y LOCAL
                empleadoEncontrado.setRol(empleadoLogueado.getRol());
                empleadoEncontrado.setLocal(empleadoLogueado.getLocal());
                // SI ES OTRO USUARIO, VALIDAR ROL DEL USUARIO QUE REALIZA LA OPERACIÓN PARA CAMBIAR EL ROL
            } else {
                switch (empleadoLogueado.getRol().getAuthority()) {
                    case "ROLE_SYSADMIN" -> {
                        empleadoEncontrado.setRol(roleService.findById(usuarioDTO.getRol()).orElseThrow());
                        empleadoEncontrado.setLocal(localService.findById(usuarioDTO.getLocal()).orElseThrow());
                    }
                    case "ROLE_ADMIN" -> {
                        empleadoEncontrado.setRol(roleService.findById(3L).orElseThrow());
                        empleadoEncontrado.setLocal(empleadoLogueado.getLocal());
                    }
                }
                // VALIDACIONES DE ROL Y LOCAL
                if ("ROLE_EMPLEADO".equals(roleService.findById(usuarioDTO.getRol()).orElseThrow().getAuthority()) &&
                        localService.findById(usuarioDTO.getLocal()).orElseThrow().getId() != 1) {
                    empleadoEncontrado.setRol(roleService.findById(usuarioDTO.getRol()).orElseThrow());
                    empleadoEncontrado.setLocal(localService.findById(usuarioDTO.getLocal()).orElseThrow());
                } else if ("ROLE_USUARIO".equals(roleService.findById(usuarioDTO.getRol()).orElseThrow().getAuthority()) &&
                        localService.findById(usuarioDTO.getLocal()).orElseThrow().getId() == 1) {
                    empleadoEncontrado.setRol(roleService.findById(usuarioDTO.getRol()).orElseThrow());
                    empleadoEncontrado.setLocal(localService.findById(usuarioDTO.getLocal()).orElseThrow());
                } else if ("ROLE_ADMIN".equals(roleService.findById(usuarioDTO.getRol()).orElseThrow().getAuthority()) &&
                        usuarioService.existsAdminInLocal(usuarioDTO.getLocal()).isPresent() &&
                        localService.findById(usuarioDTO.getLocal()).orElseThrow().getId() != 1) {
                    empleadoEncontrado.setRol(roleService.findById(usuarioDTO.getRol()).orElseThrow());
                    empleadoEncontrado.setLocal(localService.findById(usuarioDTO.getLocal()).orElseThrow());
                } else {
                    response.put("mensaje", "Lo sentimos, el rol y/o local asignados son inválidos o no están disponibles" +
                            " actualmente para este empleado");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
            }
            empleadoEncontrado.setUsuario(usuarioDTO.getUsuario()); // SOLO LECTURA?
            usuarioService.save(empleadoEncontrado);
        } catch (NoSuchElementException | DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de actualizar el empleado");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Empleado actualizado");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de actualización de perfil válido para todos los usuarios", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Perfil actualizado"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "El usuario no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de actualizar el perfil. Inténtelo mas tarde")})
    @PutMapping(value = "/usuarios/perfil", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO', 'ROLE_USER')")
    public ResponseEntity<?> editarPerfil(@RequestBody UsuarioDTO usuarioDTO, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByEmail(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        try {
            // VALIDO SI EL PERFIL ES SOLO Y UNICAMENTE MIO
            if (usuarioDTO.getEmail().equals(usuarioLogueado.getEmail())) {
                usuarioLogueado.setNombres(usuarioDTO.getNombres());
                usuarioLogueado.setApellidoMaterno(usuarioDTO.getApellidoMaterno());
                usuarioLogueado.setApellidoPaterno(usuarioDTO.getApellidoPaterno());
                usuarioLogueado.setDni(usuarioDTO.getDni()); // SOLO LECTURA?
                usuarioLogueado.setDireccion(usuarioDTO.getDireccion());
                usuarioLogueado.setCelular(usuarioDTO.getCelular());
                usuarioLogueado.setEmail(usuarioDTO.getEmail()); // SOLO LECTURA?
                usuarioLogueado.setFotoUsuario(usuarioDTO.getFotoUsuario());
                // PARA CAMBIAR EL USUARIO O EL ROL, ES NECESARIO AUTENTICARME DE NUEVO
                // LA VARIABLE CAMBIOIMPORTANTE ME PERMITE SABER SI TENGO QUE CERRAR SESIÓN O NO DEBIDO A LOS CAMBIOS
                response.put("cambioImportante", usuarioLogueado.getUsuario().equals(usuarioDTO.getUsuario()));
                usuarioLogueado.setUsuario(usuarioDTO.getUsuario());
                if (usuarioDTO.getRol() == null && usuarioDTO.getLocal() == null) {
                    usuarioService.save(usuarioLogueado);
                } else {
                    response.put("mensaje", "Lo sentimos, no tienes acceso a este recurso");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
            } else {
                response.put("mensaje", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (NoSuchElementException | DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de actualizar el perfil");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Usuario actualizado");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de cambio de contraseña del usuario logueado en ese momento (dentro del sistema)", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Contraseña actualizada"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de actualizar la contraseña. Inténtelo mas tarde")})
    @PutMapping(value = "/usuarios/cambiar-password", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO', 'ROLE_USER')")
    public ResponseEntity<?> cambiarContrasenaUsuario(@RequestBody ChangePassword dtoPassword,
                                                      Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioAntiguo = usuarioService.findByEmail(userDetails.getUsername()).orElseThrow();
        dtoPassword.setId(usuarioAntiguo.getId());
        try {
            if (dtoPassword.getPasswordActual().isBlank() || dtoPassword.getNuevaPassword().isBlank()
                    || dtoPassword.getConfirmarPassword().isBlank()) {
                response.put("mensaje", "Rellenar todos los campos");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            usuarioService.cambiarPassword(dtoPassword);
        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, el usuario no existe");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de actualizar la contraseña");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de actualizar la contraseña. Inténtelo mas tarde");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Contraseña actualizada");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de deshabilitación del usuario mediante el id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Usuario deshabilitado"),
            @ApiResponse(code = 201, message = " "), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "El usuario no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de deshabilitar el usuario. Inténtelo mas tarde")})
    @PutMapping(value = "/usuarios/{id}/deshabilitar-cuenta", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<?> deshabilitarUsuario(@PathVariable String id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByEmail(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        Usuario usuarioEncontrado;
        try {
            if (id.matches("^\\d+$")) {
                usuarioEncontrado = usuarioService.findById(Long.parseLong(id)).orElseThrow();
            } else {
                response.put("mensaje", "Lo sentimos, el id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            if (usuarioLogueado.getRol().getAuthority().equals("ROLE_USER") &&
                    !usuarioEncontrado.getId().equals(usuarioLogueado.getId())) {
                response.put("mensaje", "Lo sentimos, no tienes acceso a este recurso");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            if (!usuarioEncontrado.isActivo()) {
                response.put("mensaje", "Lo sentimos, la cuenta ya está deshabilitada");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            usuarioEncontrado.setActivo(false);
            usuarioService.save(usuarioEncontrado);

            Map<String, Object> model = new HashMap<>();
            model.put("titulo", "Usuario Deshabilitado");
            model.put("from", "Biblioteca2020 " + "<" + emailFrom + ">");
            model.put("usuario", usuarioEncontrado.getUsuario());
            model.put("to", usuarioEncontrado.getEmail());
            model.put("subject", "Usuario Deshabilitado | Biblioteca2020");
            emailService.enviarEmail(model);

        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, el usuario no existe");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de deshabilitar el usuario");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (MessagingException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de enviar el correo de confirmación");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Usuario deshabilitado");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de habilitación de un usuario mediante el id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Usuario habilitado"),
            @ApiResponse(code = 201, message = " "), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "El usuario no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de habilitar el usuario. Inténtelo mas tarde")})
    @PutMapping(value = "/usuarios/{id}/habilitar-cuenta", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<?> habilitarUsuario(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        Usuario usuarioEncontrado;
        try {
            if (id.matches("^\\d+$")) {
                usuarioEncontrado = usuarioService.findById(Long.parseLong(id)).orElseThrow();
            } else {
                response.put("mensaje", "Lo sentimos, el id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            if (usuarioEncontrado.isActivo()) {
                response.put("mensaje", "Lo sentimos, la cuenta ya está habilitada");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            usuarioEncontrado.setActivo(true);
            usuarioService.save(usuarioEncontrado);

        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, el usuario no existe");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de habilitar el usuario");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Usuario habilitado");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*@ApiOperation(value = "Método de eliminación del usuario mediante el id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Usuario eliminado"),
            @ApiResponse(code = 201, message = " "), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "El usuario no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de eliminar el usuario. Inténtelo mas tarde")})
    @DeleteMapping(value = "/usuarios/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    public ResponseEntity<?> eliminarUsuario(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (id.matches("^\\d+$")) {
                usuarioService.delete(Long.parseLong(id));
            } else {
                response.put("mensaje", "Lo sentimos, el id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            response.put("mensaje", "Usuario eliminado");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NoSuchElementException | DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de eliminar el usuario");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/

}
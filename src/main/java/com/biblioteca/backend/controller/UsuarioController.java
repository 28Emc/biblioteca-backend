package com.biblioteca.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import com.biblioteca.backend.model.TokenConfirma;
import com.biblioteca.backend.model.Usuario;
import com.biblioteca.backend.model.dto.AccountRecovery;
import com.biblioteca.backend.model.dto.ChangePassword;
import com.biblioteca.backend.service.ITokenConfirmaService;
import com.biblioteca.backend.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@CrossOrigin(origins = { "*", "http://localhost:4200" })
@RestController
@Api(value = "usuario", description = "Operaciones referentes a los usuarios")
public class UsuarioController {

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private ITokenConfirmaService tokenConfirmaService;

    @ApiOperation(value = "Método de listado de usuarios", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Usuarios encontrados"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar los usuarios. Inténtelo mas tarde") })
    @GetMapping(value = "/listar-usuarios", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    public ResponseEntity<?> listarUsuarios() {
        Map<String, Object> response = new HashMap<>();
        List<Usuario> usuarios = null;
        try {
            usuarios = usuarioService.findAll();
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de buscar los usuarios!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        response.put("usuarios", usuarios);
        response.put("mensaje", "Usuarios encontrados!");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.FOUND);
    }

    @ApiOperation(value = "Método de consulta de usuario por su id", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Usuario encontrado"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar el usuario. Inténtelo mas tarde") })
    @GetMapping(value = "/buscar-usuario/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    public ResponseEntity<?> buscarUsuario(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Usuario usuario = null;
        try {
            usuario = usuarioService.findById(id).get();
            if (usuario == null) {
                response.put("mensaje",
                        "El usuario con ID: ".concat(id.toString().concat(" no existe en la base de datos!")));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }
        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, el usuario no existe!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de buscar el usuario!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("usuario", usuario);
        response.put("mensaje", "Usuario encontrado!");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.FOUND);
    }

    @ApiOperation(value = "Método de registro de usuarios (hecho para usuarios nuevos)", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Usuario registrado"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de registrar el usuario. Inténtelo mas tarde") })
    @PostMapping(value = "/crear-usuario", produces = "application/json")
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario) {
        Map<String, Object> response = new HashMap<>();
        try {
            usuario.setActivo(true);
            usuarioService.save(usuario);
        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de registrar el usuario!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // response.put("usuario", usuario);
        response.put("mensaje", "Usuario registrado!");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de recuperación de contraseña del usuario mediante email y DNI", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Solicitud de recuperación de contraseña enviada"),
            @ApiResponse(code = 201, message = " "),
            @ApiResponse(code = 400, message = "Lo sentimos, su cuenta està deshabilitada. Ir a 'Reactivación de cuenta'"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = "Lo sentimos, el DNI y/o correo ingresados son incorrectos"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de enviar la solicitud. Inténtelo mas tarde") })
    @PostMapping(value = "/recuperar-cuenta/recuperar-password", produces = "application/json")
    public ResponseEntity<?> enviarSolicitudRecuperacionContraseñaUsuario(
            @RequestBody AccountRecovery dtoAccountRecovery) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Usuario> usuario = usuarioService.findByNroDocumentoAndEmail(dtoAccountRecovery.getNroDocumento(),
                    dtoAccountRecovery.getEmail());
            if (!usuario.isPresent()) {
                response.put("mensaje", "Lo sentimos, el DNI y/o correo ingresados son incorrectos!");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            } else if (!usuario.get().isActivo()) {
                response.put("mensaje", "Lo sentimos, su cuenta està deshabilitada. Ir a 'Reactivación de cuenta'");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
            TokenConfirma tokenConfirma = new TokenConfirma(usuario.get(), "RECUPERAR CONTRASEÑA");
            tokenConfirmaService.save(tokenConfirma);
            response.put("tokenConfirma", tokenConfirma.getTokenConfirma());
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

            /* TODO : AQUÍ VA LA LÓGICA DE ENVÍO DEL CORREO DE CONFIRMACIÓN */

        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de enviar la solicitud! Inténtelo mas tarde");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "Solicitud de recuperación de contraseña enviada!");

        /*
         * TODO : REDIRECCIONAR A PANTALLA DE CONFIRMA DE ENVÍO DE CORREO (SE PUEDE
         * REDIRECCIONAR EN ANGULAR) O SIMPLEMENTE MANDAR EL MENSAJE DE CONFIRMA
         */

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de validación de token de recuperación de contraseña de usuario", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Token validado"),
            @ApiResponse(code = 201, message = " "), @ApiResponse(code = 400, message = " "),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = "Lo sentimos, el enlace es inválido o el token ya caducó"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de procesar la solicitud. Inténtelo mas tarde") })
    @GetMapping(value = "/recuperar-cuenta/recuperar-password/confirmar-token", produces = "application/json")
    public ResponseEntity<?> validarTokenRecuperacionContraseñaUsuario(@RequestParam("token") String token) {
        Map<String, Object> response = new HashMap<>();
        ChangePassword dtoPassword = null;
        try {
            TokenConfirma tokenConfirma = tokenConfirmaService.findByTokenConfirma(token).get();
            if (token != null) {
                dtoPassword = new ChangePassword();
                Usuario usuario = usuarioService.findByEmail(tokenConfirma.getUsuario().getEmail()).get();
                dtoPassword.setId(usuario.getId());
                response.put("changePassword", dtoPassword);
            }
        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, el enlace es inválido o el token ya caducó!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de enviar la solicitud! Inténtelo mas tarde");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "Token validado!");

        /*
         * TODO : REDIRECCIONAR A PANTALLA DE ACTUALIZACIÓN DE CONTRASEÑA (SE PUEDE
         * REDIRECCIONAR EN ANGULAR)
         */

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de cambio de contraseña del usuario (afuera del sistema)", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Contraseña recuperada y actualizada"),
            @ApiResponse(code = 400, message = "Rellenar todos los campos"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de actualizar la contraseña. Inténtelo mas tarde") })
    @PostMapping(value = "/recuperar-cuenta/recuperar-password/confirmar-token", produces = "application/json")
    public ResponseEntity<?> recuperarContraseñaUsuario(@RequestBody ChangePassword dtoPassword) {
        Map<String, Object> response = new HashMap<>();
        //Usuario usuarioNuevo = null;
        //Usuario usuarioAntiguo = null;
        dtoPassword.setPasswordActual(null);
        try {
            if (dtoPassword.getNuevaPassword().equals("") || dtoPassword.getConfirmarPassword().equals("")) {
                response.put("mensaje", "Rellenar todos los campos!");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
            //usuarioNuevo = usuarioService.findById(dtoPassword.getId()).get();
            //response.put("usuarioAntiguo", usuarioAntiguo);
            //usuarioAntiguo = usuarioNuevo;
            usuarioService.recuperarPassword(dtoPassword);

            /*
             * TODO : ELIMINAR EL TOKEN UTILIZADO DE LA BBDD AL MOMENTO DE RECUPERAR LA CONTRASEÑA
             */
            
            /*
             * TODO : AQUÍ VA LA LÓGICA DE ENVÍO DEL CORREO DE CONFIRMACIÓN DE RECUPERACIÓN
             * DE CONTRASEÑA
             */

        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de actualizar la contraseña!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de actualizar la contraseña!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //response.put("usuarioNuevo", usuarioNuevo);
        response.put("mensaje", "Contraseña recuperada y actualizada!");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de actualización de usuarios", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Usuario actualizado"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "El usuario no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de actualizar el usuario. Inténtelo mas tarde") })
    @PutMapping(value = "/editar-usuario/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO', 'ROLE_USER')")
    public ResponseEntity<?> editarUsuario(@RequestBody Usuario usuario, @PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Usuario usuarioEncontrado = null;
        try {
            usuarioEncontrado = usuarioService.findById(id).get();
            if (usuarioEncontrado == null) {
                response.put("mensaje",
                        "El usuario con ID: ".concat(id.toString().concat(" no existe en la base de datos!")));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }
            usuarioEncontrado.setNombres(usuario.getNombres());
            usuarioEncontrado.setApellidoMaterno(usuario.getApellidoMaterno());
            usuarioEncontrado.setApellidoPaterno(usuario.getApellidoPaterno());
            usuarioEncontrado.setNroDocumento(usuario.getNroDocumento());
            usuarioEncontrado.setDireccion(usuario.getDireccion());
            usuarioEncontrado.setCelular(usuario.getCelular());
            usuarioEncontrado.setEmail(usuario.getEmail());
            usuarioEncontrado.setUsuario(usuario.getUsuario());
            usuarioEncontrado.setFotoUsuario(usuario.getFotoUsuario());
            usuarioService.save(usuarioEncontrado);
        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, el usuario no existe!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de actualizar el usuario!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // response.put("usuario", usuarioEncontrado);
        response.put("mensaje", "Usuario actualizado!");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de cambio de contraseña del usuario (dentro del sistema)", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Contraseña actualizada"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de actualizar la contraseña. Inténtelo mas tarde") })
    @PutMapping(value = "/cambiar-password", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO', 'ROLE_USER')")
    public ResponseEntity<?> cambiarContraseñaUsuario(@RequestBody ChangePassword dtoPassword,
            Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioAntuguo = usuarioService.findByEmail(userDetails.getUsername()).get();
        // response.put("usuarioAntuguo", usuarioAntuguo);
        dtoPassword.setId(usuarioAntuguo.getId());
        try {
            if (dtoPassword.getPasswordActual().equals("") || dtoPassword.getNuevaPassword().equals("")
                    || dtoPassword.getConfirmarPassword().equals("")) {
                response.put("mensaje", "Rellenar todos los campos!");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
            usuarioService.cambiarPassword(dtoPassword);
        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, el usuario no existe!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de actualizar la contraseña!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de actualizar la contraseña!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Usuario usuarioActualizado =
        // usuarioService.findByEmail(userDetails.getUsername()).get();
        // response.put("usuarioActualizado", usuarioActualizado);
        response.put("mensaje", "Contraseña actualizada!");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de deshabilitación del usuario mediante el id", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Usuario deshabilitado"),
            @ApiResponse(code = 201, message = " "), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "El usuario no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de deshabilitar el usuario. Inténtelo mas tarde") })
    @PutMapping(value = "/deshabilitar-usuario/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<?> deshabilitarUsuario(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Usuario usuarioEncontrado = null;
        try {
            usuarioEncontrado = usuarioService.findById(id).get();
            if (usuarioEncontrado == null) {
                response.put("mensaje",
                        "El usuario con ID: ".concat(id.toString().concat(" no existe en la base de datos!")));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }
            usuarioEncontrado.setActivo(false);
            usuarioService.save(usuarioEncontrado);
        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, el usuario no existe!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de deshabilitar el usuario!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("usuario", usuarioEncontrado);
        response.put("mensaje", "Usuario deshabilitado!");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de eliminación del usuario mediante el id", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Usuario eliminado"),
            @ApiResponse(code = 201, message = " "), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "El usuario no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de eliminar el usuario. Inténtelo mas tarde") })
    @DeleteMapping(value = "/eliminar-usuario/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        // Usuario usuarioEncontrado = null;
        try {
            // usuarioEncontrado = usuarioService.findById(id).get();
            // response.put("usuario", usuarioEncontrado);
            usuarioService.delete(id);
            response.put("mensaje", "Usuario eliminado!");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, el usuario no existe!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de eliminar el usuario!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO', 'ROLE_USER')")
    @GetMapping(value = "/cancelar")
    public String cancelar() {
        return "redirect:/biblioteca";
    }

}
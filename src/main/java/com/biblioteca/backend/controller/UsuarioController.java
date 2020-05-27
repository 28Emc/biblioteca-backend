package com.biblioteca.backend.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import com.biblioteca.backend.model.Usuario;
import com.biblioteca.backend.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@Api(value = "usuario", description = "Operaciones referentes a los usuarios")
public class UsuarioController {

    @Autowired
    private IUsuarioService usuarioService;

    @ApiOperation(value = "Método de listado de usuarios", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Usuarios encontrados"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar los usuarios. Inténtelo mas tarde") })
    @GetMapping(value = "/listar-usuarios", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    public ResponseEntity<?> listarUsuarios() {
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("usuario", usuarioService.findAll());
            response.put("mensaje", "Usuarios encontrados!");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.FOUND);
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de buscar los usuarios!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
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
        try {
            response.put("usuario", usuarioService.findById(id).get());
            response.put("mensaje", "Usuario encontrado!");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.FOUND);
        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, el usuario no existe!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de buscar el usuario!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Método de registro de usuarios", response = ResponseEntity.class)
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
            response.put("usuario", usuario);
            response.put("mensaje", "Usuario registrado!");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de registrar el usuario!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
            response.put("usuario", usuarioEncontrado);
            response.put("mensaje", "Usuario actualizado!");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, el usuario no existe!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de actualizar el usuario!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
            usuarioEncontrado.setActivo(false);
            usuarioService.save(usuarioEncontrado);
            response.put("usuario", usuarioEncontrado);
            response.put("mensaje", "Usuario deshabilitado!");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, el usuario no existe!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de deshabilitar el usuario!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
        Usuario usuarioEncontrado = null;
        try {
            usuarioEncontrado = usuarioService.findById(id).get();
            response.put("usuario", usuarioEncontrado);
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
    public String cancelarPerfil() {
        return "redirect:/biblioteca";
    }

}
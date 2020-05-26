package com.biblioteca.backend.controller;

import java.util.HashMap;
import java.util.Map;
import com.biblioteca.backend.model.Usuario;
import com.biblioteca.backend.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
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

    @ApiOperation(value = "Método de registro de usuarios", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Usuario registrado"),
            @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de realizar el registro. Inténtelo mas tarde") })
    @PostMapping(value = "/crear-usuario", produces = "application/json")
    //@PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
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
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
    }

}
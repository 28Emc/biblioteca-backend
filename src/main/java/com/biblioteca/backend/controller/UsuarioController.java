package com.biblioteca.backend.controller;

import java.util.HashMap;
import java.util.Map;

import com.biblioteca.backend.model.Usuario;
import com.biblioteca.backend.service.IUsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsuarioController {

    @Autowired
    private IUsuarioService usuarioService;

    @PostMapping("/crear-usuario")
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
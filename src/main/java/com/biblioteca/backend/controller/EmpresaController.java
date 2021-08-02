package com.biblioteca.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.biblioteca.backend.model.Empresa.Empresa;
import com.biblioteca.backend.service.IEmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@CrossOrigin(origins = {"*", "http://localhost:4200"})
@RestController
@Api(value = "empresa", description = "Operaciones de consulta de la empresa")
public class EmpresaController {

    @Autowired
    private IEmpresaService empresaService;

    @ApiOperation(value = "Método de listado de empresas", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Empresas encontradas"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar las empresas. Inténtelo mas tarde")})
    @GetMapping(value = "/empresas", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> listarEmpresas() {
        Map<String, Object> response = new HashMap<>();
        List<Empresa> empresas;
        try {
            empresas = empresaService.findAll();
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de buscar las empresas");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("message", "Empresas encontradas: ".concat(String.valueOf(empresas.size())));
        response.put("data", empresas);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de consulta de empresa por su ruc", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Empresa encontrada"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar la empresa. Inténtelo mas tarde")})
    @GetMapping(value = "/empresas/{ruc}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> buscarEmpresaPorRuc(@PathVariable String ruc) {
        Map<String, Object> response = new HashMap<>();
        Empresa empresa;
        try {
            if (!ruc.matches("^\\d{11}$")) {
                response.put("message", "Lo sentimos, el ruc es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            empresa = empresaService.findByRuc(ruc).orElseThrow();
        } catch (NoSuchElementException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de buscar la empresa");
            response.put("error", "La empresa no existe");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de buscar la empresa");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Empresa encontrada");
        response.put("data", empresa);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
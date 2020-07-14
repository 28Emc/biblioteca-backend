package com.biblioteca.backend.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.biblioteca.backend.model.Local.DTO.LocalDTO;
import com.biblioteca.backend.model.Local.Local;
import com.biblioteca.backend.model.Usuario.Usuario;
import com.biblioteca.backend.service.ILocalService;
import com.biblioteca.backend.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
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
@Api(value = "local", description = "Operaciones referentes a los locales")
public class LocalController {

    @Autowired
    private ILocalService localService;

    @Autowired
    private IUsuarioService usuarioService;

    @ApiOperation(value = "Método de listado de locales", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Locales encontrados"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar los locales. Inténtelo mas tarde")})
    @GetMapping(value = "/locales", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    public ResponseEntity<?> listarLocales() {
        Map<String, Object> response = new HashMap<>();
        List<Local> locales;
        try {
            locales = localService.findAll();
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de buscar los locales!");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("locales", locales);
        response.put("mensaje", "Locales encontrados!");
        return new ResponseEntity<>(response, HttpStatus.FOUND);
    }

    @ApiOperation(value = "Método de consulta de local por su id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Local encontrado"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar el local. Inténtelo mas tarde")})
    @GetMapping(value = "/locales/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    public ResponseEntity<?> buscarLocal(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Local local;
        try {
            local = localService.findById(id).orElseThrow();
        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, el local no existe!");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de buscar el local!");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("local", local);
        response.put("mensaje", "Local encontrado!");
        return new ResponseEntity<>(response, HttpStatus.FOUND);
    }

    @ApiOperation(value = "Método de registro de locales", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Local registrado"),
            @ApiResponse(code = 400, message = "Lo sentimos, la dirección ya está asociada a otro local"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de registrar el local. Inténtelo mas tarde")})
    @PostMapping(value = "/locales", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    public ResponseEntity<?> crearLocal(@RequestBody LocalDTO localDTO, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new NoSuchElementException("El usuario no existe")
        );
        Map<String, Object> response = new HashMap<>();
        Local local = new Local();
        try {
            if (localService.existsByDireccion(localDTO.getDireccion())) {
                response.put("mensaje", "Lo sentimos, la dirección ya está asociada a otro local!");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else {
                local.setDireccion(localDTO.getDireccion());
                local.setInfoAdicional(localDTO.getInfoAdicional());
                local.setEmpresa(usuarioLogueado.getLocal().getEmpresa());
                localService.save(local);
            }
        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de registrar el local!");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Local registrado!");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de actualización de locales", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Local actualizado"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "El local no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de actualizar el local. Inténtelo mas tarde")})
    @PutMapping(value = "/locales/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    public ResponseEntity<?> editarLocal(@RequestBody LocalDTO localDTO, @PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Optional<Local> localEncontrado;
        try {
            localEncontrado = localService.findById(id);
            localEncontrado.get().setDireccion(localDTO.getDireccion());
            localEncontrado.get().setInfoAdicional(localDTO.getInfoAdicional());
            //LA EMPRESA SOLO LA PUEDE EDITAR EL SYSADMIN DE LA MISMA EMPRESA (CON SU LOCAL)
            //localEncontrado.get().setEmpresa(localDTO.getEmpresa());
            // SI EL ESTADO DEL LOCAL ES FALSO, DEBO DESHABILITAR SUS "HIJOS" (EMPLEADOS Y LIBROS)
            localService.save(localEncontrado.get());
        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, el local no existe!");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de actualizar el local!");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Local actualizado!");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // LA ELIMINACIÓN DE UN LOCAL NO ESTÁ CONTEMPLADA POR RELACIONES FORÁNEAS CON
    // OTRAS TABLAS COMO EMPLEADOS Y LIBROS
    @ApiOperation(value = "Método de deshabilitación del local mediante el id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Local deshabilitado"),
            @ApiResponse(code = 201, message = " "), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "El local no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de deshabilitar el local. Inténtelo mas tarde")})
    @PutMapping(value = "/locales/{id}/deshabilitar-local", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    public ResponseEntity<?> deshabilitarLocal(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Optional<Local> localEncontrado;
        List<Usuario> usuariosTotales;
        List<Usuario> usuariosActivos = new ArrayList<>();
        List<Usuario> usuariosInactivos = new ArrayList<>();
        try {
            localEncontrado = localService.findById(id);
            // - BUSCO LOS USUARIOS (MEJOR DICHO, USUARIOS CON ROLE_EMPLEADO) DE ESE LOCAL
            // - RECORRO LA LISTA DE LOS USUARIOS ENCONTRADOS
            // - SEPARO LOS ACTIVOS Y LOS INACTIVOS
            // - LOS USUARIOS ACTIVOS SE INACTIVAN Y ALMACENAN EN UN LISTADO
            // - LOS USUARIOS INACTIVOS SE AGREGAN A OTRO LISTADO
            // - GUARDO LOS CAMBIOS
            // NOTA PERSONAL : VER SI ESTE MÈTODO ES EFICIENTE O NO
            usuariosTotales = usuarioService.findByLocal(id);
            usuariosTotales.forEach(u -> {
                if (u.isActivo()) {
                    usuariosActivos.add(u);
                    u.setActivo(false);
                    usuarioService.saveUser(u, "LOCAL DESHABILITADO");
                } else {
                    usuariosInactivos.add(u);
                }
            });
            response.put("usuariosDelLocalActivos", usuariosActivos.size());
            response.put("usuariosDelLocalInactivos", usuariosInactivos.size());
            localEncontrado.get().setActivo(false);
            localService.save(localEncontrado.get());
        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, el local no existe!");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de deshabilitar el local!");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Local deshabilitado!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
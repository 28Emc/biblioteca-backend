package com.biblioteca.backend.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import com.biblioteca.backend.model.Local;
import com.biblioteca.backend.model.Usuario;
import com.biblioteca.backend.service.ILocalService;
import com.biblioteca.backend.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@Api(value = "local", description = "Operaciones referentes a los locales")
public class LocalController {

    @Autowired
    private ILocalService localService;

    @Autowired
    private IUsuarioService usuarioService;

    @ApiOperation(value = "Método de listado de locales", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Locales encontrados"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar los locales. Inténtelo mas tarde") })
    @GetMapping(value = "/locales", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    public ResponseEntity<?> listarLocales() {
        Map<String, Object> response = new HashMap<>();
        List<Local> locales = null;
        try {
            locales = localService.findAll();
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de buscar los locales!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        response.put("locales", locales);
        response.put("mensaje", "Locales encontrados!");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.FOUND);
    }

    @ApiOperation(value = "Método de consulta de local por su id", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Local encontrado"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar el local. Inténtelo mas tarde") })
    @GetMapping(value = "/locales/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    public ResponseEntity<?> buscarLocal(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Local local = null;
        try {
            local = localService.findById(id).get();
            if (local == null) {
                response.put("mensaje",
                        "El local con ID: ".concat(id.toString().concat(" no existe en la base de datos!")));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }
        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, el local no existe!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de buscar el local!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("local", local);
        response.put("mensaje", "Local encontrado!");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.FOUND);
    }

    @ApiOperation(value = "Método de registro de locales", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Local registrado"),
            @ApiResponse(code = 400, message = "Lo sentimos, la dirección ya está asociada a otro local"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de registrar el local. Inténtelo mas tarde") })
    @PostMapping(value = "/locales", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    public ResponseEntity<?> crearLocal(@RequestBody Local local) {
        Map<String, Object> response = new HashMap<>();
        Optional<Local> localEncontrado = null;
        try {
            localEncontrado = localService.findByDireccion(local.getDireccion());
            if (localEncontrado.isPresent()) {
                response.put("mensaje", "Lo sentimos, la dirección ya está asociada a otro local!");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            } else {
                local.setActivo(true);
                localService.save(local);
            }
        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de registrar el local!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Local registrado!");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de actualización de locales", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Local actualizado"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "El local no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de actualizar el local. Inténtelo mas tarde") })
    @PutMapping(value = "/locales/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    public ResponseEntity<?> editarLocal(@RequestBody Local local, @PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Local localEncontrado = null;
        try {
            localEncontrado = localService.findById(id).get();
            if (localEncontrado == null) {
                response.put("mensaje",
                        "El local con ID: ".concat(id.toString().concat(" no existe en la base de datos!")));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }
            localEncontrado.setDireccion(local.getDireccion());
            localEncontrado.setObservaciones(local.getObservaciones());
            localEncontrado.setEmpresa(local.getEmpresa());

            // SI EL ESTADO DEL LOCAL ES FALSO, DEBO DESHABILITAR SUS "HIJOS" (EMPLEADOS Y LIBROS)

            localService.save(local);
        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, el local no existe!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de actualizar el local!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Local actualizado!");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    // LA ELIMINACIÓN DE UN LOCAL NO ESTÁ CONTEMPLADA POR RELACIONES FORÁNEAS CON
    // OTRAS TABLAS COMO EMPLEADOS Y LIBROS
    @ApiOperation(value = "Método de deshabilitación del local mediante el id", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Local deshabilitado"),
            @ApiResponse(code = 201, message = " "), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "El local no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de deshabilitar el local. Inténtelo mas tarde") })
    @PutMapping(value = "/locales/{id}/deshabilitar-local", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    public ResponseEntity<?> deshabilitarLocal(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Local localEncontrado = null;
        List<Usuario> usuariosTotales = new ArrayList<Usuario>();
        List<Usuario> usuariosActivos = new ArrayList<Usuario>();
        List<Usuario> usuariosInactivos = new ArrayList<Usuario>();
        try {
            localEncontrado = localService.findById(id).get();
            if (localEncontrado == null) {
                response.put("mensaje",
                        "El local con ID: ".concat(id.toString().concat(" no existe en la base de datos!")));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }
            // - BUSCO LOS USUARIOS (MEJOR DICHO, USUARIOS CON ROLE_EMPLEADO) DE ESE LOCAL
            // - RECORRO LA LISTA DE LOS USUARIOS ENCONTRADOS
            // - SEPARO LOS ACTIVOS Y LOS INACTIVOS
            // - LOS USUARIOS ACTIVOS SE INACTIVAN Y ALMACENAN EN UN LISTADO
            // - LOS USUARIOS INACTIVOS SE AGREGAN A OTRO LISTADO
            // - GUARDO LOS CAMBIOS
            // NOTA PERSONAL : VER SI ESTE MÈTODO ES EFICIENTE O NO
            usuariosTotales = usuarioService.findByLocal(id);
            usuariosTotales.stream().forEach(u -> {
                if (u.isActivo()) {
                    usuariosActivos.add(u);
                    u.setActivo(false);
                    usuarioService.save(u);
                } else {                   
                    usuariosInactivos.add(u);
                }
            });
            response.put("usuariosDelLocalActivos", usuariosActivos.size());
            response.put("usuariosDelLocalInactivos", usuariosInactivos.size());
            localEncontrado.setActivo(false);
            localService.save(localEncontrado);
        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, el local no existe!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de deshabilitar el local!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Local deshabilitado!");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

}
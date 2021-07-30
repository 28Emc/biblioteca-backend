package com.biblioteca.backend.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import com.biblioteca.backend.model.Libro.Libro;
import com.biblioteca.backend.model.Local.DTO.LocalDTO;
import com.biblioteca.backend.model.Local.Local;
import com.biblioteca.backend.model.Usuario.Usuario;
import com.biblioteca.backend.service.IEmpresaService;
import com.biblioteca.backend.service.ILibroService;
import com.biblioteca.backend.service.ILocalService;
import com.biblioteca.backend.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
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

import javax.validation.Valid;

@CrossOrigin(origins = {"*", "http://localhost:4200"})
@RestController
@Api(value = "local", description = "Operaciones referentes a los locales")
public class LocalController {

    @Autowired
    private IEmpresaService empresaService;

    @Autowired
    private ILocalService localService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private ILibroService libroService;

    @ApiOperation(value = "Método de listado de locales", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Locales encontrados"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar los locales. " +
                    "Inténtelo mas tarde")})
    @GetMapping(value = "/locales", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    public ResponseEntity<?> listarLocales() {
        Map<String, Object> response = new HashMap<>();
        List<Local> locales;

        try {
            // VER SI EXCLUIR EL LOCAL CON ID "1" (RESERVADO PARA LOS USUARIOS)
            locales = localService.findAll();
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de buscar los locales");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        response.put("locales", locales);
        response.put("message", "Locales encontrados");
        return new ResponseEntity<>(response, HttpStatus.FOUND);
    }

    @ApiOperation(value = "Método de consulta de local por su id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Local encontrado"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar el local. " +
                    "Inténtelo mas tarde")})
    @GetMapping(value = "/locales/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    public ResponseEntity<?> buscarLocal(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        Local local;

        try {
            // EXCLUYO EL LOCAL CON ID "1" PORQUE NO PUEDE SER UTILIZADO
            // PARA LA LÓGICA DEL MANTENIMIENTO
            if (id.matches("^\\d+$") && !id.equals("1")) {
                local = localService.findById(Long.parseLong(id)).orElseThrow();
            } else {
                response.put("message", "El id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (NoSuchElementException e) {
            response.put("message", "El local no existe");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de buscar el local");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("local", local);
        response.put("message", "Local encontrado");
        return new ResponseEntity<>(response, HttpStatus.FOUND);
    }

    @ApiOperation(value = "Método de registro de locales", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Local registrado"),
            @ApiResponse(code = 400, message = "La dirección ya está asociada a otro local"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de registrar el local." +
                    " Inténtelo mas tarde")})
    @PostMapping(value = "/locales", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    public ResponseEntity<?> crearLocal(@Valid @RequestBody LocalDTO localDTO, BindingResult result,
                                        Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = usuarioService.findByUsuario(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        Optional<Local> localEncontrado;

        try {
            if (result.hasErrors()) {
                List<String> errores = result.getFieldErrors()
                        .stream()
                        .map(error -> error.getField() + " : " + error.getDefaultMessage())
                        .collect(Collectors.toList());
                response.put("message", errores);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            localEncontrado = localService.findByDireccion(localDTO.getDireccion());

            if (localEncontrado.isPresent()) {
                response.put("message", "La dirección ya está asociada a otro local");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else {
                Local local = new Local();
                local.setDireccion(localDTO.getDireccion().trim());
                // SYSADMIN CREA LOCALES DE LA EMPRESA PERTENECIENTE SOLAMENTE

                /* TODO: REVISAR
                local.setIdEmpresa(usuarioLogueado.getLocal().getIdEmpresa());*/
                localService.save(local);
            }

        } catch (DataIntegrityViolationException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de registrar el local");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Local registrado");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de actualización de locales", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Local actualizado"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "El local no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de actualizar el local." +
                    " Inténtelo mas tarde")})
    @PutMapping(value = "/locales/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    public ResponseEntity<?> editarLocal(@Valid @RequestBody LocalDTO localDTO, BindingResult result,
                                         @PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        Local localEncontrado;

        try {
            if (id.matches("^\\d+$") && !id.equals("1")) {
                localEncontrado = localService.findById(Long.parseLong(id)).orElseThrow();
            } else {
                response.put("message", "El id es inválido");
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

            if (localService.findByDireccion(localDTO.getDireccion()).isPresent()) {
                response.put("message",
                        "Lo sentimos, el local con esa dirección ya existe");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else {
                localEncontrado.setDireccion(localDTO.getDireccion().trim());
                localService.save(localEncontrado);
            }

        } catch (NoSuchElementException e) {
            response.put("message", "Lo sentimos, el local no existe");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de actualizar el local");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Local actualizado");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // LA ELIMINACIÓN DE UN LOCAL NO ESTÁ CONTEMPLADA POR RELACIONES FORÁNEAS CON
    // OTRAS TABLAS COMO EMPLEADOS Y LIBROS
    @ApiOperation(value = "Método de deshabilitación del local mediante el id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Local deshabilitado"),
            @ApiResponse(code = 201, message = " "), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "El local no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de deshabilitar el local." +
                    " Inténtelo mas tarde")})
    @PutMapping(value = "/locales/{id}/deshabilitar", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    public ResponseEntity<?> deshabilitarLocal(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        Local localEncontrado;
        List<Usuario> usuariosTotales;
        List<Usuario> usuariosActivos = new ArrayList<>();
        List<Usuario> usuariosInactivos = new ArrayList<>();
        List<Libro> librosTotales;
        List<Libro> librosActivos = new ArrayList<>();
        List<Libro> librosInactivos = new ArrayList<>();

        try {
            if (id.matches("^\\d+$") && !id.equals("1")) {
                localEncontrado = localService.findById(Long.parseLong(id)).orElseThrow();
            } else {
                response.put("message", "El id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            if (!localEncontrado.isActivo()) {
                response.put("message", "El local ya está deshabilitado");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // - BUSCO LOS USUARIOS (MEJOR DICHO, USUARIOS CON ROLE_EMPLEADO) DE ESE LOCAL
            // - RECORRO LA LISTA DE LOS USUARIOS ENCONTRADOS
            // - SEPARO LOS ACTIVOS Y LOS INACTIVOS
            // - LOS USUARIOS ACTIVOS SE INACTIVAN Y ALMACENAN EN UN LISTADO
            // - LOS USUARIOS INACTIVOS SE AGREGAN A OTRO LISTADO
            // - GUARDO LOS CAMBIOS
            // EL MISMO PROCEDIMIENTO SE APLICA PARA LOS LIBROS
            /* TODO: REVISAR
            usuariosTotales = usuarioService.findByLocal(Long.parseLong(id));
            usuariosTotales.forEach(u -> {
                if (u.isActivo()) {
                    usuariosActivos.add(u);
                    u.setActivo(false);
                    usuarioService.save(u);
                } else {
                    usuariosInactivos.add(u);
                }
            });*/

            librosTotales = libroService.findByLocal(Long.parseLong(id));
            librosTotales.forEach(l -> {
                if (l.isActivo()) {
                    librosActivos.add(l);
                    l.setActivo(false);
                    libroService.save(l);
                } else {
                    librosInactivos.add(l);
                }
            });

            /*response.put("usuariosDelLocalActivos", usuariosActivos.size());
            response.put("usuariosDelLocalInactivos", usuariosInactivos.size());
            response.put("librosDelLocalActivos", librosActivos.size());
            response.put("librosDelLocalInactivos", librosInactivos.size());*/
            localEncontrado.setActivo(false);
            localService.save(localEncontrado);
        } catch (NoSuchElementException e) {
            response.put("message", "El local no existe");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de deshabilitar el local");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Local deshabilitado");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de habilitación del local mediante el id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Local habilitado"),
            @ApiResponse(code = 201, message = " "), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "El local no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de habilitar el local." +
                    " Inténtelo mas tarde")})
    @PutMapping(value = "/locales/{id}/habilitar", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    public ResponseEntity<?> habilitarLocal(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        Local localEncontrado;
        List<Usuario> usuariosTotales;
        List<Usuario> usuariosActivos = new ArrayList<>();
        List<Usuario> usuariosInactivos = new ArrayList<>();
        List<Libro> librosTotales;
        List<Libro> librosActivos = new ArrayList<>();
        List<Libro> librosInactivos = new ArrayList<>();

        try {
            if (id.matches("^\\d+$") && !id.equals("1")) {
                localEncontrado = localService.findById(Long.parseLong(id)).orElseThrow();
            } else {
                response.put("message", "Lo sentimos, el id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            if (localEncontrado.isActivo()) {
                response.put("message", "El local ya está habilitado");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            /* TODO: REVISAR
            usuariosTotales = usuarioService.findByLocal(Long.parseLong(id));
            usuariosTotales.forEach(u -> {
                if (!u.isActivo()) {
                    usuariosInactivos.add(u);
                    u.setActivo(true);
                    usuarioService.save(u);
                } else {
                    usuariosActivos.add(u);
                }
            });*/

            librosTotales = libroService.findByLocal(Long.parseLong(id));
            librosTotales.forEach(l -> {
                if (!l.isActivo()) {
                    librosInactivos.add(l);
                    l.setActivo(true);
                    libroService.save(l);
                } else {
                    librosActivos.add(l);
                }
            });

            /*response.put("usuariosDelLocalActivos", usuariosActivos.size());
            response.put("usuariosDelLocalInactivos", usuariosInactivos.size());
            response.put("librosDelLocalActivos", librosActivos.size());
            response.put("librosDelLocalInactivos", librosInactivos.size());*/
            localEncontrado.setActivo(true);
            localService.save(localEncontrado);
        } catch (NoSuchElementException e) {
            response.put("message", "El local no existe");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de habilitar el local");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Local habilitado");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
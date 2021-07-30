package com.biblioteca.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import com.biblioteca.backend.model.Libro.DTO.LibroDTO;
import com.biblioteca.backend.model.Libro.Libro;
import com.biblioteca.backend.model.Local.Local;
import com.biblioteca.backend.model.Usuario.Usuario;
import com.biblioteca.backend.service.ICategoriaService;
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
@Api(value = "libro", description = "Operaciones referentes a los libros")
public class LibroController {

    @Autowired
    private ILibroService libroService;

    @Autowired
    private ICategoriaService categoriaService;

    @Autowired
    private ILocalService localService;

    @Autowired
    private IUsuarioService empleadoService;

    @ApiOperation(value = "Método de listado de libros", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Libros encontrados"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar los libros. " +
                    "Inténtelo mas tarde")})
    @GetMapping(value = {"/libros", "/biblioteca"}, produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO', 'ROLE_USUARIO')")
    public ResponseEntity<?> listarLibros(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = empleadoService.findByUsuario(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        List<Libro> libros;
        try {
            switch (usuarioLogueado.getRol().getAuthority()) {
                // MUESTRO TODOS LOS LIBROS
                // MUESTRO LOS LIBROS DEL MISMO LOCAL DEL ADMIN
                case "ROLE_SYSADMIN":
                case "ROLE_ADMIN":
                    libros = libroService.findAll();
                    response.put("libros", libros);
                    break;
                // MUESTRO LOS LIBROS DEL MISMO LOCAL DEL EMPLEADO
                /*case "ROLE_EMPLEADO":
                    libros = libroService.fetchByIdWithLocales(usuarioLogueado.getLocal().getId());
                    response.put("libros", libros);
                    break;*/
                // MUESTRO LA BIBLIOTECA (LIBROS MOSTRADOS A SYSADMIN, PERO ÚNICOS)
                case "ROLE_USUARIO":
                    libros = libroService.findAllDistinct();
                    response.put("data", libros);
                    break;
            }
        } catch (NoSuchElementException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de buscar los libros");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de buscar los libros. Inténtelo mas tarde");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("message", "Libros encontrados");
        return new ResponseEntity<>(response, HttpStatus.FOUND);
    }

    @ApiOperation(value = "Método de consulta de libro por su id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Libro encontrado"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = "El id es invalido"), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar el libro." +
                    " Inténtelo mas tarde")})
    @GetMapping(value = "/libros/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    public ResponseEntity<?> buscarLibro(@PathVariable String id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario empleadoLogueado = empleadoService.findByUsuario(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        Libro libro;
        try {
            // EL SYSADMIN BUSCA ENTRE TODOS LOS LIBROS (DE CUALQUIER LOCAL), Y VE SI EXISTE
            // LOS DEMAS USUARIOS VAN A FILTRAR EL LIBRO POR SU LOCAL DE PERTENENCIA
            if (id.matches("^\\d+$")) {
                libro = libroService.findById(Long.parseLong(id)).orElseThrow();
            } else {
                response.put("message", "El id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            response.put("data", libro);
            response.put("message", "Libro encontrado");
            return new ResponseEntity<>(response, HttpStatus.OK);

            // ADMIN Y EMPLEADOS PUEDE VER LIBROS DE SU PROPIO LOCAL SOLAMENTE
            /*if (!empleadoLogueado.getRol().getAuthority().equals("ROLE_SYSADMIN")
                    && !libro.getLocal().equals(empleadoLogueado.getLocal())) {
                response.put("message",
                        "El libro con ID: ".concat(id.concat(" no existe en tu local de pertenencia")));
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else {
                response.put("libro", libro);
                response.put("message", "Libro encontrado");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }*/
        } catch (NoSuchElementException e) {
            response.put("message", "El libro no existe");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de buscar el libro. Inténtelo mas tarde");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Método de registro de libros", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Libro registrado"),
            @ApiResponse(code = 400, message = "El libro ya existe"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de registrar el libro. " +
                    "Inténtelo mas tarde")})
    @PostMapping(value = "/libros", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    public ResponseEntity<?> crearLibro(@Valid @RequestBody LibroDTO libroDTO, BindingResult result,
                                        Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario empleadoLogueado = empleadoService.findByUsuario(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        Optional<Libro> libroEncontrado;
        Local local;

        try {

            if (result.hasErrors()) {
                List<String> errores = result.getFieldErrors()
                        .stream()
                        .map(error -> error.getField() + " : " + error.getDefaultMessage())
                        .collect(Collectors.toList());
                response.put("message", errores);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // EL SYSADMIN BUSCA ENTRE TODOS LOS LIBROS (DE CUALQUIER LOCAL), Y VE SI EXISTE
            // LOS DEMAS USUARIOS VAN A FILTRAR EL LIBRO POR SU LOCAL DE PERTENENCIA
            local = localService.findById(libroDTO.getIdLocal()).orElseThrow();
            Libro libro = new Libro();
            if ("ROLE_ADMIN".equals(empleadoLogueado.getRol().getAuthority())) {
                libroEncontrado = libroService.findByTituloLikeIgnoreCase(libroDTO.getTitulo());
                if (libroEncontrado.isPresent()) {
                    response.put("message", "Lo sentimos, el libro ya existe");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
                libro.setLocal(localService.findById(libroDTO.getIdLocal()).orElseThrow());
                    /*case "ROLE_ADMIN":
                case "ROLE_EMPLEADO":
                    if (local.getId().equals(empleadoLogueado.getIdLocal().getId())) {
                        libroEncontrado = libroService.findByTituloAndLocal(libroDTO.getTitulo(),
                                empleadoLogueado.getIdLocal().getId());
                        if (libroEncontrado.isPresent()) {
                            response.put("message", "Lo sentimos, el libro ya existe");
                            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                        }
                        libro.setLocal(empleadoLogueado.getLocal());
                    }
                    break;*/
            }

            if (libro.validateIsbn13(libroDTO.getISBN())) {
                libro.setISBN(libroDTO.getISBN());
            } else {
                response.put("message", "El codigo ISBN es invalido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            libro.setTitulo(libroDTO.getTitulo());
            libro.setAutor(libroDTO.getAutor());
            libro.setDescripcion(libroDTO.getDescripcion());
            libro.setFechaPublicacion(libroDTO.getFechaPublicacion());
            libro.setStock(libroDTO.getStock());
            libro.setFotoLibro(libroDTO.getFotoLibro());
            libro.setCategoria(categoriaService.findById(libroDTO.getIdCategoria()).orElseThrow());
            libroService.save(libro);
        } catch (Exception e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de registrar el libro!");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Libro registrado!");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de actualización de libros", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Libro Actualizado"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "El libro no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de actualizar el libro. Inténtelo mas tarde")})
    @PutMapping(value = "/libros/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<?> editarLibro(@Valid @RequestBody LibroDTO libroDTO, BindingResult result,
                                         @PathVariable String id, Authentication authentication) throws Exception {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario empleadoLogueado = empleadoService.findByUsuario(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        Libro libroEncontrado;

        try {

            if (result.hasErrors()) {
                List<String> errores = result.getFieldErrors()
                        .stream()
                        .map(error -> error.getField() + " : " + error.getDefaultMessage())
                        .collect(Collectors.toList());
                response.put("message", errores);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            if (id.matches("^\\d+$")) {
                libroEncontrado = libroService.findById(Long.parseLong(id)).orElseThrow();
            } else {
                response.put("message", "El id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // VERIFICO QUE EL ADMIN NO TENGA ACCESO A UN LIBRO QUE ESTÉ FUERA DE SU LOCAL
            // DE PERTENENCIA
            /*if (!empleadoLogueado.getRol().getAuthority().equals("ROLE_SYSADMIN")
                    && !libroEncontrado.getLocal().equals(empleadoLogueado.getLocal())) {
                response.put("message",
                        "El libro con ID: ".concat(id.toString().concat(" no existe en tu local de pertenencia")));
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }*/

            if (libroEncontrado.validateIsbn13(libroDTO.getISBN())) {
                libroEncontrado.setISBN(libroDTO.getISBN());
            } else {
                response.put("message", "El codigo ISBN es invalido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            libroEncontrado.setTitulo(libroDTO.getTitulo());
            libroEncontrado.setAutor(libroDTO.getAutor());
            libroEncontrado.setDescripcion(libroDTO.getDescripcion());
            libroEncontrado.setFechaPublicacion(libroDTO.getFechaPublicacion());
            libroEncontrado.setStock(libroDTO.getStock());
            libroEncontrado.setFotoLibro(libroDTO.getFotoLibro());

            libroEncontrado.setLocal(localService.findById(libroDTO.getIdLocal()).orElseThrow(() ->
                    new Exception("El local no existe")));

            libroEncontrado.setCategoria(categoriaService.findById(libroDTO.getIdCategoria()).orElseThrow(() ->
                    new Exception("La categoría no existe")));
            libroService.save(libroEncontrado);
        } catch (NoSuchElementException | DataIntegrityViolationException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de actualizar el libro");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Libro Actualizado");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de deshabilitación del libro mediante el id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Libro deshabilitado"),
            @ApiResponse(code = 201, message = " "), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "La categoría no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de deshabilitar el libro." +
                    " Inténtelo mas tarde")})
    @PutMapping(value = "/libros/{id}/deshabilitar", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<?> deshabilitarLibro(@PathVariable String id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario empleadoLogueado = empleadoService.findByUsuario(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        Libro libroEncontrado;

        try {

            if (id.matches("^\\d+$")) {
                libroEncontrado = libroService.findById(Long.parseLong(id)).orElseThrow();
            } else {
                response.put("message", "El id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            if (!libroEncontrado.isActivo()) {
                response.put("message", "El libro ya está deshabilitado");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            /*if (!empleadoLogueado.getRol().getAuthority().equals("ROLE_SYSADMIN")
                    && !libroEncontrado.getLocal().equals(empleadoLogueado.getLocal())) {
                response.put("message",
                        "El libro con ID: ".concat(id.concat(" no existe en tu local de pertenencia")));
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }*/

            libroEncontrado.setActivo(false);
            libroService.save(libroEncontrado);
        } catch (NoSuchElementException | DataIntegrityViolationException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de deshabilitar el libro");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Libro deshabilitado");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Método de habilitación del libro mediante el id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Libro habilitado"),
            @ApiResponse(code = 201, message = " "), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "La categoría no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de habilitar el libro." +
                    " Inténtelo mas tarde")})
    @PutMapping(value = "/libros/{id}/habilitar", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<?> habilitarLibro(@PathVariable String id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario empleadoLogueado = empleadoService.findByUsuario(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        Libro libroEncontrado;

        try {

            if (id.matches("^\\d+$")) {
                libroEncontrado = libroService.findById(Long.parseLong(id)).orElseThrow();
            } else {
                response.put("message", "El id es inválido");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            if (libroEncontrado.isActivo()) {
                response.put("message", "El libro ya está habilitado");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            /*if (!empleadoLogueado.getRol().getAuthority().equals("ROLE_SYSADMIN")
                    && !libroEncontrado.getLocal().equals(empleadoLogueado.getLocal())) {
                response.put("message",
                        "El libro con ID: ".concat(id.concat(" no existe en tu local de pertenencia")));
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }*/

            libroEncontrado.setActivo(true);
            libroService.save(libroEncontrado);
        } catch (NoSuchElementException | DataIntegrityViolationException e) {
            response.put("message", "Lo sentimos, hubo un error a la hora de habilitar el libro");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("message", "Libro habilitado");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
package com.biblioteca.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.biblioteca.backend.model.Libro.Libro;
import com.biblioteca.backend.model.Local.Local;
import com.biblioteca.backend.model.Usuario.Usuario;
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
@Api(value = "libro", description = "Operaciones referentes a los libros")
public class LibroController {

    @Autowired
    private ILibroService libroService;

    // @Autowired
    // private ICategoriaService categoriaService;

    @Autowired
    private ILocalService localService;

    @Autowired
    private IUsuarioService empleadoService;

    @ApiOperation(value = "Método de listado de libros", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Libros encontrados"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar los libros. Inténtelo mas tarde")})
    @GetMapping(value = {"/libros", "/biblioteca"}, produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO', 'ROLE_USUARIO')")
    public ResponseEntity<?> listarLibros(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuarioLogueado = empleadoService.findByEmail(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        List<Libro> libros;
        try {
            switch (usuarioLogueado.getRol().getAuthority()) {
                // MUESTRO TODOS LOS LIBROS
                case "ROLE_SYSADMIN":
                    libros = libroService.findAll();
                    response.put("libros", libros);
                    break;
                // MUESTRO LOS LIBROS DEL MISMO LOCAL DEL ADMIN
                case "ROLE_ADMIN":
                    // MUESTRO LOS LIBROS DEL MISMO LOCAL DEL EMPLEADO
                case "ROLE_EMPLEADO":
                    libros = libroService.fetchByIdWithLocales(usuarioLogueado.getLocal().getId());
                    response.put("libros", libros);
                    break;
                // MUESTRO LA BIBLIOTECA (LIBROS MOSTRADOS A SYSADMIN, PERO ÚNICOS)
                case "ROLE_USUARIO":
                    libros = libroService.findAllDistinct();
                    response.put("libros", libros);
                    break;
            }
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de buscar los libros!");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.put("mensaje", "Libros encontrados!");
        return new ResponseEntity<>(response, HttpStatus.FOUND);
    }

    @ApiOperation(value = "Método de consulta de libro por su id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Libro encontrado"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar el libro. Inténtelo mas tarde")})
    @GetMapping(value = "/libros/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    public ResponseEntity<?> buscarLibro(@PathVariable Long id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario empleadoLogueado = empleadoService.findByEmail(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        Libro libro;
        try {
            // EL SYSADMIN BUSCA ENTRE TODOS LOS LIBROS (DE CUALQUIER LOCAL), Y VE SI EXISTE
            // LOS DEMAS USUARIOS VAN A FILTRAR EL LIBRO POR SU LOCAL DE PERTENENCIA
            libro = libroService.findById(id).orElseThrow();
            if (libro == null) {
                response.put("mensaje",
                        "El libro con ID: ".concat(id.toString().concat(" no existe en la base de datos!")));
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
                // ADMIN Y EMPLEADOS PUEDE VER LIBROS DE SU PROPIO LOCAL SOLAMENTE
            } else if (!empleadoLogueado.getRol().getAuthority().equals("ROLE_SYSADMIN")
                    && !libro.getLocal().equals(empleadoLogueado.getLocal())) {
                response.put("mensaje",
                        "El libro con ID: ".concat(id.toString().concat(" no existe en tu local de pertenencia!")));
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else {
                response.put("libro", libro);
                response.put("mensaje", "Libro encontrado!");
                return new ResponseEntity<>(response, HttpStatus.FOUND);
            }
        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, el libro no existe!");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de buscar el libro!");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Método de registro de libros", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Libro registrado"),
            @ApiResponse(code = 400, message = "Lo sentimos, el libro ya existe"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de registrar el libro. Inténtelo mas tarde")})
    @PostMapping(value = "/libros", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    public ResponseEntity<?> crearLibro(@RequestBody Libro libro, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario empleadoLogueado = empleadoService.findByEmail(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        Optional<Libro> libroEncontrado;
        Local local;
        try {
            // EL SYSADMIN BUSCA ENTRE TODOS LOS LIBROS (DE CUALQUIER LOCAL), Y VE SI EXISTE
            // LOS DEMAS USUARIOS VAN A FILTRAR EL LIBRO POR SU LOCAL DE PERTENENCIA
            local = localService.findById(libro.getLocal().getId()).orElseThrow();
            switch (empleadoLogueado.getRol().getAuthority()) {
                case "ROLE_SYSADMIN":
                    libroEncontrado = libroService.findByTituloLikeIgnoreCase(libro.getTitulo());
                    if (libroEncontrado.isPresent()) {
                        response.put("mensaje", "Lo sentimos, el libro ya existe!");
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                    libro.setActivo(true);
                    break;
                case "ROLE_ADMIN":
                case "ROLE_EMPLEADO":
                    if (local.getId().equals(empleadoLogueado.getLocal().getId())) {
                        libroEncontrado = libroService.findByTituloAndLocal(libro.getTitulo(),
                                empleadoLogueado.getLocal().getId());
                        if (libroEncontrado.isPresent()) {
                            response.put("mensaje", "Lo sentimos, el libro ya existe!");
                            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                        }
                        libro.setLocal(empleadoLogueado.getLocal());
                    }
                    break;
            }
            libroService.save(libro);
        } catch (DataIntegrityViolationException | NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de registrar el libro!");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Libro registrado!");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de actualización de libros", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Libro Actualizado"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "El libro no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de actualizar el libro. Inténtelo mas tarde")})
    @PutMapping(value = "/libros/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<?> editarLibro(@RequestBody Libro libro, @PathVariable Long id,
                                         Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario empleadoLogueado = empleadoService.findByEmail(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        Libro libroEncontrado;
        try {
            libroEncontrado = libroService.findById(id).orElseThrow();
            // VERIFICO QUE EL ADMIN NO TENGA ACCESO A UN LIBRO QUE ESTÉ FUERA DE SU LOCAL
            // DE
            // PERTENENCIA
            if (!empleadoLogueado.getRol().getAuthority().equals("ROLE_SYSADMIN")
                    && !libroEncontrado.getLocal().equals(empleadoLogueado.getLocal())) {
                response.put("mensaje",
                        "El libro con ID: ".concat(id.toString().concat(" no existe en tu local de pertenencia!")));
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            libroEncontrado.setISBN(libro.getISBN());
            libroEncontrado.setTitulo(libro.getTitulo());
            libroEncontrado.setAutor(libro.getAutor());
            libroEncontrado.setDescripcion(libro.getDescripcion());
            libroEncontrado.setFechaPublicacion(libro.getFechaPublicacion());
            libroEncontrado.setStock(libro.getStock());
            libroEncontrado.setFotoLibro(libro.getFotoLibro());
            // REPITO LA LÓGICA DEL REGISTRO AL NO SER DE ROL SYSADMIN
            if (!empleadoLogueado.getRol().getAuthority().equals("ROLE_SYSADMIN")) {
                libroEncontrado.setLocal(empleadoLogueado.getLocal());
            } else {
                libroEncontrado.setLocal(libro.getLocal());
            }
            libroEncontrado.setCategoria(libro.getCategoria());
            libroService.save(libroEncontrado);
        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, el libro no existe!");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de actualizar el libro!");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Libro Actualizado!");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de deshabilitación del libro mediante el id", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Libro deshabilitado"),
            @ApiResponse(code = 201, message = " "), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "La categoría existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de deshabilitar el libro. Inténtelo mas tarde")})
    @PutMapping(value = "/libros/{id}/deshabilitar", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<?> deshabilitarLibro(@PathVariable Long id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario empleadoLogueado = empleadoService.findByEmail(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        Libro libroEncontrado;
        try {
            libroEncontrado = libroService.findById(id).orElseThrow();
            if (!empleadoLogueado.getRol().getAuthority().equals("ROLE_SYSADMIN")
                    && !libroEncontrado.getLocal().equals(empleadoLogueado.getLocal())) {
                response.put("mensaje",
                        "El libro con ID: ".concat(id.toString().concat(" no existe en tu local de pertenencia!")));
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            libroEncontrado.setActivo(false);
            libroService.save(libroEncontrado);
        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, el libro no existe!");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un rror a la hora de deshabilitar el libro!");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Libro Deshabilitado!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
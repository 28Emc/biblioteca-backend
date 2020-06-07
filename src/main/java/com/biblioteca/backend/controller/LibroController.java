package com.biblioteca.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import com.biblioteca.backend.model.Libro;
import com.biblioteca.backend.model.Usuario;
import com.biblioteca.backend.service.ILibroService;
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

@CrossOrigin(origins = { "*", "http://localhost:4200" })
@RestController
@Api(value = "libro", description = "Operaciones referentes a los libros")
public class LibroController {

    @Autowired
    private ILibroService libroService;

    // @Autowired
    // private ICategoriaService categoriaService;

    // @Autowired
    // private ILocalService localService;

    @Autowired
    private IUsuarioService empleadoService;

    @ApiOperation(value = "Método de listado de libros", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Libros encontrados"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar los libros. Inténtelo mas tarde") })
    @GetMapping(value = "/libros", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN')")
    public ResponseEntity<?> listarLibros() {
        Map<String, Object> response = new HashMap<>();
        List<Libro> libros = null;
        try {
            libros = libroService.findAll();
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de buscar los libros!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }
        response.put("libros", libros);
        response.put("mensaje", "Libros encontrados!");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.FOUND);
    }

    @ApiOperation(value = "Método de consulta de libro por su id", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 302, message = "Libro encontrado"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de buscar el libro. Inténtelo mas tarde") })
    @GetMapping(value = "/libros/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    public ResponseEntity<?> buscarLibro(@PathVariable Long id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario empleadoLogueado = empleadoService.findByEmail(userDetails.getUsername()).get();
        Map<String, Object> response = new HashMap<>();
        Libro libro = null;
        try {
            libro = libroService.findById(id).get();
            if (libro == null) {
                response.put("mensaje",
                        "El libro con ID: ".concat(id.toString().concat(" no existe en la base de datos!")));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            } else if (!libro.getLocal().equals(empleadoLogueado.getLocal())) {
                response.put("mensaje",
                        "El libro con ID: ".concat(id.toString().concat(" no existe en tu local de pertenencia!")));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }
        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, el libro no existe!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de buscar el libro!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("libro", libro);
        response.put("mensaje", "Libro encontrado!");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.FOUND);
    }

    @ApiOperation(value = "Método de registro de libros", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Libro registrado"),
            @ApiResponse(code = 400, message = "Lo sentimos, el libro ya existe"),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de registrar el libro. Inténtelo mas tarde") })
    @PostMapping(value = "/libros", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO')")
    public ResponseEntity<?> crearLibro(@RequestBody Libro libro, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario empleadoLogueado = empleadoService.findByEmail(userDetails.getUsername()).get();
        Map<String, Object> response = new HashMap<>();
        Libro libroEncontrado = null;
        try {
            // EL SYSADMIN BUSCA ENTRE TODOS LOS LIBROS (DE CUALQUIER LOCAL), Y VE SI EXISTE
            // LOS DEMAS USUARIOS VAN A FILTRAR EL LIBRO POR SU LOCAL DE PERTENENCIA
            switch (empleadoLogueado.getRol().getAuthority()) {
                case "[ROLE_SYSADMIN]":
                    libroEncontrado = libroService.findByTituloLikeIgnoreCase(libro.getTitulo()).iterator().next();
                    break;
                default:
                    libroEncontrado = libroService
                            .findByTituloAndLocal(libro.getTitulo(), empleadoLogueado.getLocal().getId()).get();
                    break;
            }
            if (libroEncontrado != null) {
                response.put("mensaje", "Lo sentimos, el libro ya existe!");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            } else if (!empleadoLogueado.getRol().getAuthority().equals("[ROLE_SYSADMIN]")) {
                libro.setLocal(empleadoLogueado.getLocal());
            }
            libro.setActivo(true);
            libroService.save(libro);
        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de registrar el libro!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Libro registrado!");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de actualización de libros", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = " "),
            @ApiResponse(code = 201, message = "Libro Actualizado"), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "El libro no existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de actualizar el libro. Inténtelo mas tarde") })
    @PutMapping(value = "/libros/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<?> editarLibro(@RequestBody Libro libro, @PathVariable Long id,
            Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario empleadoLogueado = empleadoService.findByEmail(userDetails.getUsername()).get();
        Map<String, Object> response = new HashMap<>();
        Libro libroEncontrado = null;
        try {
            libroEncontrado = libroService.findById(id).get();
            if (libroEncontrado == null) {
                response.put("mensaje",
                        "El libro con ID: ".concat(id.toString().concat(" no existe en la base de datos!")));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
                // VERIFICO QUE EL ADMIN NO TENGA ACCESO A UN LIBRO QUE ESTÉ FUERA DE SU LOCAL
                // DE
                // PERTENENCIA
            } else if (!libroEncontrado.getLocal().equals(empleadoLogueado.getLocal())) {
                response.put("mensaje",
                        "El libro con ID: ".concat(id.toString().concat(" no existe en tu local de pertenencia!")));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }
            libroEncontrado.setTitulo(libro.getTitulo());
            libroEncontrado.setAutor(libro.getAutor());
            libroEncontrado.setDescripcion(libro.getDescripcion());
            libroEncontrado.setFechaPublicacion(libro.getFechaActualizacion());
            libroEncontrado.setStock(libro.getStock());
            libroEncontrado.setFotoLibro(libro.getFotoLibro());
            // REPITO LA LÓGICA DEL REGISTRO AL NO SER DE ROL SYSADMIN
            if (!empleadoLogueado.getRol().getAuthority().equals("[ROLE_SYSADMIN]")) {
                libroEncontrado.setLocal(empleadoLogueado.getLocal());
            } else {
                libroEncontrado.setLocal(libro.getLocal());
            }
            libroEncontrado.setCategoria(libro.getCategoria());
            libroService.save(libroEncontrado);
        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, el libro no existe!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un error a la hora de actualizar el libro!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Libro Actualizado!");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Método de deshabilitación del libro mediante el id", response = ResponseEntity.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Libro deshabilitado"),
            @ApiResponse(code = 201, message = " "), @ApiResponse(code = 401, message = " "),
            @ApiResponse(code = 403, message = " "), @ApiResponse(code = 404, message = "La categoría existe"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de deshabilitar el libro. Inténtelo mas tarde") })
    @PutMapping(value = "/libros/{id}/deshabilitar", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN')")
    public ResponseEntity<?> deshabilitarLibro(@PathVariable Long id, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario empleadoLogueado = empleadoService.findByEmail(userDetails.getUsername()).get();
        Map<String, Object> response = new HashMap<>();
        Libro libroEncontrado = null;
        try {
            libroEncontrado = libroService.findById(id).get();
            if (libroEncontrado == null) {
                response.put("mensaje",
                        "El libro con ID: ".concat(id.toString().concat(" no existe en la base de datos!")));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            } else if (!libroEncontrado.getLocal().equals(empleadoLogueado.getLocal())) {
                response.put("mensaje",
                        "El libro con ID: ".concat(id.toString().concat(" no existe en tu local de pertenencia!")));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }
            libroEncontrado.setActivo(false);
            libroService.save(libroEncontrado);
        } catch (NoSuchElementException e) {
            response.put("mensaje", "Lo sentimos, el libro no existe!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.put("mensaje", "Lo sentimos, hubo un rror a la hora de deshabilitar el libro!");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "Libro Deshabilitado!");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

}
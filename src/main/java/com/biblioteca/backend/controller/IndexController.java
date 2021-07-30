package com.biblioteca.backend.controller;

import java.util.HashMap;
import java.util.Map;

import com.biblioteca.backend.config.security.JwtUtil;
import com.biblioteca.backend.model.Usuario.Usuario;
import com.biblioteca.backend.model.Usuario.DTO.AuthenticationRequest;
import com.biblioteca.backend.model.Usuario.DTO.AuthenticationResponse;
import com.biblioteca.backend.service.CustomUserDetailsService;
import com.biblioteca.backend.service.ILibroService;
import com.biblioteca.backend.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@CrossOrigin(origins = {"*", "http://localhost:4200"})
@RestController
@Api(value = "index", description = "Index de Biblioteca2020")
public class IndexController {

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private ILibroService libroService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    // MÉTODO PARA VALIDAR USUARIO AL LOGUEARSE
    @ApiOperation(value = "Método de login de usuario mediante email y contraseña", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Bienvenido usuario"),
            @ApiResponse(code = 400, message = "Lo sentimos, su cuenta está desactivada."),
            @ApiResponse(code = 401, message = " "), @ApiResponse(code = 403, message = " "),
            @ApiResponse(code = 404, message = "El usuario o contraseña es invàlido! Intente de nuevo"),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de realizar el login. Inténtelo mas tarde")})
    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<?> crearTokenAutenticacion(@RequestBody AuthenticationRequest authenticationRequest)
            throws Exception {
        // AUTENTICO EL USUARIO QUE INGRESA USUARIO Y CONTRASEÑA ...
        Map<String, Object> response = new HashMap<>();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(), authenticationRequest.getPassword()));
            // ... SI TIENE ÈXITO, AGREGO LOS DATOS DEL USUARIO AL OBJETO DEL TIPO
            // USERDETAILS ...
            final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
            Usuario usuario = usuarioService.findByUsuario(userDetails.getUsername()).orElseThrow();
            // ... GENERO EL TOKEN A PARTIR DEL USERDETAILS ...
            final String jwt = jwtUtil.generateToken(userDetails);
            // .. Y RETORNO EL TOKEN ...
            response.put("mensaje", "Bienvenido " + usuario.getUsuario());
            response.put("authenticationResponse", new AuthenticationResponse(jwt));
        } catch (BadCredentialsException e) {
            // ... SI FALLA LA AUTENTICACIÓN POR MALAS CREDENCIALES MANDO UN ERROR ...
            response.put("mensaje", "El usuario o contraseña es invàlido! Intente de nuevo");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DisabledException e) {
            // ... SI FALLA LA AUTENTICACIÓN POR EL ESTADO INACTIVO MANDO OTRO ERROR.
            response.put("mensaje", "Lo sentimos, su cuenta está desactivada!");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*@ApiOperation(value = "Acceso a una zona permitida solamente a los usuarios autenticados", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Bienvenido, se encuentra una zona de acceso reservada a los usuarios autenticados"),
            @ApiResponse(code = 401, message = ""),
            @ApiResponse(code = 403, message = "No tiene permiso de acceder a este recurso"),
            @ApiResponse(code = 404, message = " "),
            @ApiResponse(code = 500, message = "Lo sentimos, hubo un error a la hora de realizar la consulta a la base de datos. Inténtelo mas tarde")})
    @GetMapping(value = "/biblioteca", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SYSADMIN', 'ROLE_ADMIN', 'ROLE_EMPLEADO', 'ROLE_USER')")
    public ResponseEntity<?> verBiblioteca(Authentication authentication) {
        //UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Map<String, Object> response = new HashMap<>();
        List<Libro> libros;
        try {
            libros = libroService.findAllDistinct();
            response.put("libros", libros);
        } catch (Exception e) {
            response.put("mensaje", e);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }*/

}
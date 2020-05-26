package com.biblioteca.backend.config.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.biblioteca.backend.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

// CLASE QUE ESCUCHA CADA PETICIÓN Y EXIJE EL TOKEN PARA VALIDAR 
@Component
public class JwtFilterRequest extends OncePerRequestFilter {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // OBTENGO EL TOKEN DESDE LA CABECERA DE LA PETICIÓN ...
        final String authorizationHeader = request.getHeader("Authorization");
        // ... VALIDO SI MI TOKEN ES NULO O NO TIENE EL FORMATO CORRECTO ...
        String username = null;
        String jwt = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // ... SI EL TOKEN ES VÁLIDO, OBTENGO EL TOKEN Y EL NOMBRE DE USUARIO
            jwt = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
        }
        // SI EL USUARIO ES VÁLIDO Y NO EXISTE UN USUARIO AUTENTICADO CON MI NOMBRE,
        // CREO UN USUARIO AUTENTICADO
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            // SI EL TOKEN ES VÀLIDO DADOS EL TOKEN Y EL USUARIO AUTENTICADO NUEVO, VALIDO
            // NUEVAMENTE EL USUARIO Y LO AGREGO AL OBJETO USERDETAILS (VÀLIDO SOLAMENTE
            // PARA ESTA PETICIÓN)
            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }

}
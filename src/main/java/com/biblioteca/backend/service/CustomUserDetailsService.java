package com.biblioteca.backend.service;

import java.util.Optional;
import com.biblioteca.backend.config.security.CustomUserDetails;
import com.biblioteca.backend.model.Usuario;
import com.biblioteca.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// SERVICIO QUE CREA UN OBJETO DEL TIPO USERDETAILS SEGUN EL NOMBRE DE USUARIO QUE OBTENGO DESDE LA BBDD
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(username);

        usuario.orElseThrow(() -> new UsernameNotFoundException("Usuario " + username + " no encontrado!"));

        return usuario.map(CustomUserDetails::new).get();
    }

}
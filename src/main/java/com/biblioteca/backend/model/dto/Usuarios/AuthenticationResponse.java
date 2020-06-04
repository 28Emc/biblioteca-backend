package com.biblioteca.backend.model.dto.Usuarios;

import lombok.AllArgsConstructor;
import lombok.Getter;

// OBJETO CONTENEDOR DEL TOKEN
@AllArgsConstructor
@Getter
public class AuthenticationResponse {

    private final String token;
}
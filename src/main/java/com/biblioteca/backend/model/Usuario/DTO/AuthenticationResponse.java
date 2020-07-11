package com.biblioteca.backend.model.Usuario.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

// OBJETO CONTENEDOR DEL TOKEN
@AllArgsConstructor
@Getter
public class AuthenticationResponse {

    private final String token;
}
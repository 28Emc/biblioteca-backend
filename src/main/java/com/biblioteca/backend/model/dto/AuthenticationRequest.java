package com.biblioteca.backend.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

// OBJETO CONTENEDOR DE LAS CREDENCIALES DEL USUARIO
@Data
@NoArgsConstructor
public class AuthenticationRequest {

    private String username;
    private String password;
}
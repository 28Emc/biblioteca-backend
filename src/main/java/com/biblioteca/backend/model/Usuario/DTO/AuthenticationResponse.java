package com.biblioteca.backend.model.Usuario.DTO;

// OBJETO CONTENEDOR DEL TOKEN
public class AuthenticationResponse {

    private final String token;

    public String getToken() {
        return token;
    }

    public AuthenticationResponse(String token) {
        this.token = token;
    }
}
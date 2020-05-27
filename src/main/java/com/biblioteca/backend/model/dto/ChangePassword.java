package com.biblioteca.backend.model.dto;

import lombok.Data;

@Data
public class ChangePassword {

    private Long id;

    private String passwordActual;

    private String nuevaPassword;

    private String confirmarPassword;

    public ChangePassword() {
    }

    public ChangePassword(Long id) {
        this.id = id;
    }

}
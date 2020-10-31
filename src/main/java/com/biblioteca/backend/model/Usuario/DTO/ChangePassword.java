package com.biblioteca.backend.model.Usuario.DTO;

public class ChangePassword {

    private Long id;

    private String passwordActual;

    private String nuevaPassword;

    private String confirmarPassword;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPasswordActual() {
        return passwordActual;
    }

    public void setPasswordActual(String passwordActual) {
        this.passwordActual = passwordActual;
    }

    public String getNuevaPassword() {
        return nuevaPassword;
    }

    public void setNuevaPassword(String nuevaPassword) {
        this.nuevaPassword = nuevaPassword;
    }

    public String getConfirmarPassword() {
        return confirmarPassword;
    }

    public void setConfirmarPassword(String confirmarPassword) {
        this.confirmarPassword = confirmarPassword;
    }

    public ChangePassword() {
    }

    public ChangePassword(Long id) {
        this.id = id;
    }


    public ChangePassword(Long id, String passwordActual, String nuevaPassword, String confirmarPassword) {
        this.id = id;
        this.passwordActual = passwordActual;
        this.nuevaPassword = nuevaPassword;
        this.confirmarPassword = confirmarPassword;
    }
}
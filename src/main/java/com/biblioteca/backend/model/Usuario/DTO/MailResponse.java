package com.biblioteca.backend.model.Usuario.DTO;

public class MailResponse {
    
    private String mensaje;
    private boolean estado;

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public MailResponse() {
    }

    public MailResponse(String mensaje, boolean estado) {
        this.mensaje = mensaje;
        this.estado = estado;
    }
}
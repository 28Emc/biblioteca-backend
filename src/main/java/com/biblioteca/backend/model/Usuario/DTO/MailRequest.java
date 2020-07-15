package com.biblioteca.backend.model.Usuario.DTO;

public class MailRequest {

    private String usuario;
    private String to;
    private String from;
    private String subject;

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public MailRequest() {
    }

    public MailRequest(String usuario, String to, String from, String subject) {
        this.usuario = usuario;
        this.to = to;
        this.from = from;
        this.subject = subject;
    }
}
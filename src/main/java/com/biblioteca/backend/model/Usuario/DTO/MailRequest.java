package com.biblioteca.backend.model.Usuario.DTO;

import lombok.Data;

@Data
public class MailRequest {

    private String usuario;
    private String to;
    private String from;
    private String subject;

}
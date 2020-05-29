package com.biblioteca.backend.model.dto;

import lombok.Data;

@Data
public class MailRequest {

    private String usuario;
    private String to;
    private String from;
    private String subject;

}
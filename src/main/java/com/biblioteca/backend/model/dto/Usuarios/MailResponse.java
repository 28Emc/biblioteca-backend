package com.biblioteca.backend.model.dto.Usuarios;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailResponse {
    
    private String mensaje;
    private boolean estado;

}
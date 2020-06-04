package com.biblioteca.backend.model.dto.Usuarios;

import lombok.Data;

@Data
public class AccountRecovery {
    
    private String nroDocumento;

	private String email;
}
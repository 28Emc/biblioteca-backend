package com.biblioteca.backend.model.Usuario.DTO;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class AccountRecovery {

    @NotEmpty(message = "{notEmpty.accountRecoveryDTO.dni}")
    @Size(min = 8, max = 8, message = "{size.accountRecoveryDTO.dni}")
    private String dni;

    @NotEmpty(message = "{notEmpty.accountRecoveryDTO.email}")
    @Size(min = 5, max = 30, message = "{size.accountRecoveryDTO.email}")
    @Email(message = "{email.accountRecoveryDTO.email}")
	private String email;

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AccountRecovery() {
    }

    public AccountRecovery(String dni, String email) {
        this.dni = dni;
        this.email = email;
    }
}
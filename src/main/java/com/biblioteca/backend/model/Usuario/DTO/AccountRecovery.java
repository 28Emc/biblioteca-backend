package com.biblioteca.backend.model.Usuario.DTO;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class AccountRecovery {

    @NotEmpty(message = "{notEmpty.accountRecoveryDTO.dni}")
    @Size(min = 8, max = 8, message = "{size.accountRecoveryDTO.dni}")
    @Column(name = "nro_documento")
    private String nroDocumento;

    @NotEmpty(message = "{notEmpty.accountRecoveryDTO.email}")
    @Size(min = 5, max = 30, message = "{size.accountRecoveryDTO.email}")
    @Email(message = "{email.accountRecoveryDTO.email}")
	private String email;

    public String getNroDocumento() {
        return nroDocumento;
    }

    public void setNroDocumento(String nroDocumento) {
        this.nroDocumento = nroDocumento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AccountRecovery() {
    }

    public AccountRecovery(String nroDocumento, String email) {
        this.nroDocumento = nroDocumento;
        this.email = email;
    }
}
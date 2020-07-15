package com.biblioteca.backend.model.Usuario.DTO;

public class AccountRecovery {
    
    private String nroDocumento;

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
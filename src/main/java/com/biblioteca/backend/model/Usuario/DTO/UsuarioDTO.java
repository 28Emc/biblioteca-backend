package com.biblioteca.backend.model.Usuario.DTO;

import com.biblioteca.backend.model.Local.Local;
import com.biblioteca.backend.model.Rol;
import lombok.Data;

@Data
public class UsuarioDTO {

    // AQUI VAN LAS VALIDACIONES DEL PAQUETE STARTER-VALIDATIONS

    private Long id;

    private String nombres;

    private String apellidoMaterno;

    private String apellidoPaterno;

    private String dni;

    private String direccion;

    private String celular;

    private String email;

    private String usuario;

    private String password;

    private String passwordConfirmacion;

    private boolean isActivo;

    private String fotoUsuario;

    private Rol rol;

    private Local local;
}

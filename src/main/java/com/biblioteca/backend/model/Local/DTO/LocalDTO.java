package com.biblioteca.backend.model.Local.DTO;

import com.biblioteca.backend.model.Empresa;
import lombok.Data;

@Data
public class LocalDTO {

    private Long id;

    private String direccion;

    private String infoAdicional;

    private Empresa empresa;

}

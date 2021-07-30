package com.biblioteca.backend.service;

import java.util.List;
import java.util.Optional;
import com.biblioteca.backend.model.Empresa.Empresa;

public interface IEmpresaService {

    List<Empresa> findAll();

    Optional<Empresa> findById(Long id);

    Optional<Empresa> findByRuc(String ruc);

}
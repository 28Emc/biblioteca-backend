package com.biblioteca.backend.service;

import java.util.List;
import java.util.Optional;
import com.biblioteca.backend.model.Empresa;

public interface IEmpresaService {

    public List<Empresa> findAll();

    public Optional<Empresa> findById(Long id);

    public Optional<Empresa> findByRucAndIsActivo(String ruc, boolean isActivo);

    // public Optional<Empresa> fetchByIdWithLocales(Long id);

}
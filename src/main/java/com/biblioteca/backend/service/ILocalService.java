package com.biblioteca.backend.service;

import java.util.List;
import java.util.Optional;
import com.biblioteca.backend.model.Empresa.Empresa;
import com.biblioteca.backend.model.Local.Local;

public interface ILocalService {

    public List<Local> findAll();

    public Optional<Local> findById(Long id);

    public Optional<Local> findByDireccion(String direccion);

    //public Optional<Local> fetchByIdWithEmpresaAndUsuario(Long idEmpresa, Long idUsuario);

    public List<Local> findByIdEmpresa(Long idEmpresa);

    public Local save(Local local);

    public boolean existsByDireccion(String direccion);

}
package com.biblioteca.backend.service;

import java.util.List;
import java.util.Optional;

import com.biblioteca.backend.model.Local.DTO.LocalDTO;
import com.biblioteca.backend.model.Local.Local;

public interface ILocalService {

    List<Local> findAll();

    Optional<Local> findById(Long id);

    Optional<Local> findByDireccion(String direccion);

    //Optional<Local> fetchByIdWithEmpresaAndUsuario(Long idEmpresa, Long idUsuario);

    List<Local> findByIdEmpresa(Long idEmpresa);

    Local save(LocalDTO localDTO) throws Exception;

    void update(Long id, LocalDTO localDTO) throws Exception;

    void changeLocalState(Long id, boolean tipoOperacion) throws Exception;

    boolean existsByDireccion(String direccion);

}
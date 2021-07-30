package com.biblioteca.backend.service;

import java.util.List;
import java.util.Optional;
import com.biblioteca.backend.model.Categoria.Categoria;
import com.biblioteca.backend.model.Categoria.DTO.CategoriaDTO;

public interface ICategoriaService {

    List<Categoria> findAll();

    Optional<Categoria> findById(Long id) throws Exception;

    Optional<Categoria> findByNombre(String categoria);

    List<Categoria> findByNombreLikeIgnoreCase(String categoria);

    void save(CategoriaDTO categoriaDTO) throws Exception;

    void update(Long id, CategoriaDTO categoriaDTO) throws Exception;

    void changeCategoriaState(Long id, boolean tipoOperacion) throws Exception;
}
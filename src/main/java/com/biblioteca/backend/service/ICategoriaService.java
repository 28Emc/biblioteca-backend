package com.biblioteca.backend.service;

import java.util.List;
import java.util.Optional;
import com.biblioteca.backend.model.Categoria;

public interface ICategoriaService {

    public List<Categoria> findAll();

    public Optional<Categoria> findById(Long id);

    public Optional<Categoria> findByNombre(String categoria);

    public List<Categoria> findByNombreLikeIgnoreCase(String categoria);

    public Categoria save(Categoria categoria);

}
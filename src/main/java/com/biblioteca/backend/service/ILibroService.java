package com.biblioteca.backend.service;

import java.util.List;
import java.util.Optional;

import com.biblioteca.backend.model.Libro;

public interface ILibroService {

    // USADO
    public List<Libro> findAll();

    // USADO
    public Libro save(Libro libro);

    // USADO
    public Optional<Libro> findById(Long id);

    // USADO
    public List<Libro> findByCategoriaAndLocal(String categoria, Long localId);

    // USADO
    public List<Libro> findByIsActivo(boolean estado);

    // USADO
    public List<Libro> findByLocal(Long idLocal);

    // USADO
    public List<Libro> findByLocalAndIsActivo(Long idLocal, boolean estado);

    // USADO
    public List<Libro> findByTituloDistinct(String term);

    // USADO
    // public Optional<Libro> findByTitulo(String titulo);

    // USADO
    public List<Libro> findByTituloGroup();

    // USADO
    public List<Libro> findByTituloLikeIgnoreCase(String titulo);

    // USADO
    public List<Libro> findByTituloLikeIgnoreCaseAndLocalAndIsActivo(String term, Long id, Boolean estado);

    // USADO
    public Optional<Libro> findByTituloAndLocal(String term, Long id);

    // USADO
    public Optional<Libro> findByTituloAndLocalAndIsActivo(String term, Long id, Boolean estado);

    // USADO
    public List<Libro> fetchWithCategoriaWithLocal();

    // USADO
    public List<Libro> fetchByIdWithLocalesWithEmpleado(Long id, Long idEmpleado);

    // USADO
    public List<Libro> findByCategoria(String categoria);

}
package com.biblioteca.backend.service;

import java.util.List;
import java.util.Optional;

import com.biblioteca.backend.model.Libro.Libro;

public interface ILibroService {

    public List<Libro> findAll();

    public List<Libro> findAllDistinct();

    // USADO
    public Libro save(Libro libro);

    // USADO
    public Optional<Libro> findById(Long id);

    // USADO
    public List<Libro> findByCategoriaAndLocal(String categoria, Long idLocal);

    // USADO
    public List<Libro> findByIsActivo(boolean isActivo);

    // USADO
    public List<Libro> findByLocal(Long idLocal);

    // USADO
    public List<Libro> findByLocalAndIsActivo(Long idLocal, boolean isActivo);

    // USADO
    public List<Libro> findByTituloDistinct(String term);

    // USADO
    // public Optional<Libro> findByTitulo(String titulo);

    // USADO
    public List<Libro> findByTituloGroup();

    // USADO
    public Optional<Libro> findByTituloLikeIgnoreCase(String titulo);

    // USADO
    public List<Libro> findByTituloLikeIgnoreCaseAndLocalAndIsActivo(String term, Long id, Boolean isActivo);

    // USADO
    public Optional<Libro> findByTituloAndLocal(String term, Long idLocal);

    // USADO
    public Optional<Libro> findByTituloAndLocalAndIsActivo(String term, Long id, Boolean isActivo);

    // USADO
    public List<Libro> fetchWithCategoriaWithLocal();

    public List<Libro> fetchByIdWithLocales(Long idLocal);

    //public List<Libro> fetchByIdWithLocalesAndEmpleado(Long idLocal, Long idEmpleado);

    // USADO
    public List<Libro> findByCategoria(String categoria);

}
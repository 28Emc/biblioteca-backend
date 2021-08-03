package com.biblioteca.backend.service;

import java.util.List;
import java.util.Optional;

import com.biblioteca.backend.model.Libro.DTO.LibroDTO;
import com.biblioteca.backend.model.Libro.Libro;
import com.biblioteca.backend.model.Usuario.Usuario;

public interface ILibroService {

    List<Libro> findAll();

    List<Libro> findAllDistinct();

    Optional<Libro> findById(Long id);

    List<Libro> findByCategoriaAndLocal(String categoria, Long idLocal);

    List<Libro> findByIsActivo(boolean isActivo);

    List<Libro> findByLocal(Long idLocal);

    List<Libro> findByLocalAndIsActivo(Long idLocal, boolean isActivo);

    List<Libro> findByTituloDistinct(String term);

    //Optional<Libro> findByTitulo(String titulo);

    List<Libro> findByTituloGroup();

    List<Libro> findByTituloLikeIgnoreCase(String titulo);

    List<Libro> findByTituloLikeIgnoreCaseAndLocalAndIsActivo(String term, Long id, Boolean isActivo);

    Optional<Libro> findByTituloAndLocal(String term, Long idLocal);

    Optional<Libro> findByTituloAndLocalAndIsActivo(String term, Long id, Boolean isActivo);

    List<Libro> fetchWithCategoriaWithLocal();

    List<Libro> fetchByIdWithLocales(Long idLocal);

    //List<Libro> fetchByIdWithLocalesAndEmpleado(Long idLocal, Long idEmpleado);

    List<Libro> findByCategoria(String categoria);

    void save(Libro libro);

    void saveAdmin(LibroDTO libroDTO, Usuario empleadoLogueado) throws Exception;

    void update(Long id, LibroDTO libroDTO, Usuario empleadoLogueado) throws Exception;

    void changeLibroState(Long id, boolean tipoOperacion, Usuario empleadoLoguead) throws Exception;
}
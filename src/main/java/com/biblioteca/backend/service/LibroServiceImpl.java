package com.biblioteca.backend.service;

import java.util.List;
import java.util.Optional;
import com.biblioteca.backend.model.Libro;
import com.biblioteca.backend.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LibroServiceImpl implements ILibroService {

    @Autowired
    private LibroRepository repository;

    // USADO
    @Override
    @Transactional(readOnly = true)
    public List<Libro> findAll() {
        return repository.findAll();
    }

    // USADO
    @Override
    @Transactional
    public Libro save(Libro libro) {
        return repository.save(libro);
    }

    // USADO
    @Override
    @Transactional(readOnly = true)
    public Optional<Libro> findById(Long id) {
        return repository.findById(id);
    }

    // USADO
    @Override
    @Transactional(readOnly = true)
    public List<Libro> findByTituloLikeIgnoreCaseAndLocalAndIsActivo(String term, Long id, Boolean estado) {
        return repository.findByTituloLikeIgnoreCaseAndLocalAndIsActivo("%" + term + "%", id, estado);
    }

    // USADO
    @Override
    @Transactional(readOnly = true)
    public List<Libro> fetchByIdWithLocalesWithEmpleado(Long id, Long idEmpleado) {
        return repository.fetchByIdWithLocalesWithEmpleado(id, idEmpleado);
    }

    // USADO
    @Override
    @Transactional(readOnly = true)
    public List<Libro> findByTituloGroup() {
        return repository.findByTituloGroup();
    }

    // USADO
    @Override
    @Transactional(readOnly = true)
    public List<Libro> findByTituloLikeIgnoreCase(String titulo) {
        return repository.findByTituloLikeIgnoreCase(titulo);
    }

    // USADO
    @Override
    @Transactional(readOnly = true)
    public Optional<Libro> findByTituloAndLocalAndIsActivo(String term, Long id, Boolean estado) {
        return repository.findByTituloAndLocalAndIsActivo(term, id, estado);
    }

    // USADO
    @Override
    @Transactional(readOnly = true)
    public List<Libro> findByCategoriaAndLocal(String categoria, Long localId) {
        return repository.findByCategoriaAndLocal("%" + categoria + "%", localId);
    }

    // USADO
    @Override
    @Transactional(readOnly = true)
    public List<Libro> findByIsActivo(boolean estado) {
        return repository.findByIsActivo(estado);
    }

    // USADO
    @Override
    @Transactional(readOnly = true)
    public List<Libro> findByLocalAndIsActivo(Long idLocal, boolean estado) {
        return repository.findByLocalAndIsActivo(idLocal, estado);
    }

    // USADO
    @Override
    @Transactional(readOnly = true)
    public List<Libro> fetchWithCategoriaWithLocal() {
        return repository.fetchWithCategoriaWithLocal();
    }

    // USADO
    @Override
    @Transactional(readOnly = true)
    public List<Libro> findByLocal(Long idLocal) {
        return repository.findByLocal(idLocal);
    }

    // USADO
    @Override
    @Transactional(readOnly = true)
    public List<Libro> findByCategoria(String categoria) {
        return repository.findByCategoria(categoria);
    }

    // USADO
    @Override
    @Transactional(readOnly = true)
    public Optional<Libro> findByTituloAndLocal(String term, Long id) {
        return repository.findByTituloAndLocal(term, id);
    }

    // USADO
    @Override
    @Transactional(readOnly = true)
    public List<Libro> findByTituloDistinct(String term) {
        return repository.findByTituloDistinct("%" + term + "%");
    }

}
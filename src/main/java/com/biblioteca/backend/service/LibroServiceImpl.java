package com.biblioteca.backend.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import com.biblioteca.backend.model.Libro.Libro;
import com.biblioteca.backend.repository.core.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LibroServiceImpl implements ILibroService {

    @Autowired
    private LibroRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<Libro> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Libro> findAllDistinct() {
        return findAll().stream().filter(distinctByKey(Libro::getTitulo))
                .collect(Collectors.toList());
    }

    // MÉTODO QUE ME PERMITE FILTRAR UN LISTADO Y DEVOLVER ELEMENTOS ÚNICOS MEDIANTE
    // UN VALOR (KEY)
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    @Override
    @Transactional
    public Libro save(Libro libro) {
        return repository.save(libro);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Libro> findById(Long id) {
        return repository.findById(id);
    }

    // USADO
    @Override
    @Transactional(readOnly = true)
    public List<Libro> findByTituloLikeIgnoreCaseAndLocalAndIsActivo(String term, Long id, Boolean isActivo) {
        return repository.findByTituloLikeIgnoreCaseAndLocalAndIsActivo("%" + term + "%", id, isActivo);
    }

    /*@Override
    @Transactional(readOnly = true)
    public List<Libro> fetchByIdWithLocalesAndEmpleado(Long idLocal, Long idEmpleado) {
        return repository.fetchByIdWithLocalesAndEmpleado(idLocal, idEmpleado);
    }*/

    @Override
    @Transactional(readOnly = true)
    public List<Libro> fetchByIdWithLocales(Long idLocal) {
        return repository.fetchByIdWithLocales(idLocal);
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
    public Optional<Libro> findByTituloLikeIgnoreCase(String titulo) {
        return repository.findByTituloLikeIgnoreCase(titulo);
    }

    // USADO
    @Override
    @Transactional(readOnly = true)
    public Optional<Libro> findByTituloAndLocalAndIsActivo(String term, Long id, Boolean isActivo) {
        return repository.findByTituloAndLocalAndIsActivo(term, id, isActivo);
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
    public List<Libro> findByIsActivo(boolean isActivo) {
        return repository.findByIsActivo(isActivo);
    }

    // USADO
    @Override
    @Transactional(readOnly = true)
    public List<Libro> findByLocalAndIsActivo(Long idLocal, boolean isActivo) {
        return repository.findByLocalAndIsActivo(idLocal, isActivo);
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
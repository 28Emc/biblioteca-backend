package com.biblioteca.backend.service;

import java.util.List;
import java.util.Optional;
import com.biblioteca.backend.model.Categoria;
import com.biblioteca.backend.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoriaServiceImpl implements ICategoriaService {

    @Autowired
    private CategoriaRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Categoria> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Categoria> findByNombre(String categoria) {
        return repository.findByNombre(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> findByNombreLikeIgnoreCase(String categoria) {
        return repository.findByNombreLikeIgnoreCase(categoria);
    }

    @Override
    @Transactional
    public Categoria save(Categoria categoria) {
        return repository.save(categoria);
    }

}
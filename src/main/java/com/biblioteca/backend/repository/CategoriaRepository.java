package com.biblioteca.backend.repository;

import java.util.List;
import java.util.Optional;

import com.biblioteca.backend.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    public List<Categoria> findByNombreLikeIgnoreCase(String categoria);
    
    public Optional<Categoria> findByNombre(String categoria);

}
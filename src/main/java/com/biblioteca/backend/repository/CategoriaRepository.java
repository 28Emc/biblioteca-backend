package com.biblioteca.backend.repository;

import java.util.List;
import java.util.Optional;

import com.biblioteca.backend.model.Categoria.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByNombreLikeIgnoreCase(String categoria);

    Optional<Categoria> findByNombre(String categoria);

}
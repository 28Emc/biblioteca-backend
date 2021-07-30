package com.biblioteca.backend.repository.core;

import java.util.List;
import java.util.Optional;

import com.biblioteca.backend.model.Categoria.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByNombreLikeIgnoreCase(String categoria);

    Optional<Categoria> findByNombre(String categoria);

}
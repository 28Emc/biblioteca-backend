package com.biblioteca.backend.services;

import com.biblioteca.backend.models.entities.Category;

import java.util.List;
import java.util.Optional;

public interface ICategoryService {
    public List<Category> findAll();

    public Optional<Category> findById(Long id);

    public Optional<Category> findByName(String name);

    public Category save(Category category);
}

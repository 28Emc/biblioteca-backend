package com.emc.bibliotecamscore.services;

import com.emc.bibliotecamscore.models.dtos.UpdateStatusDTO;
import com.emc.bibliotecamscore.models.entities.Category;

import java.util.List;
import java.util.Optional;

public interface ICategoryService {
    List<Category> findAll();

    Optional<Category> findById(Long id);

    Optional<Category> findByName(String name);

    Category save(Category category);

    void updateStatus(Long id, UpdateStatusDTO updateStatusDTO);
}

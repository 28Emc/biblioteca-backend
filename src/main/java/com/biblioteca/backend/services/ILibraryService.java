package com.biblioteca.backend.services;

import com.biblioteca.backend.models.dtos.UpdateStatusDTO;
import com.biblioteca.backend.models.entities.Library;

import java.util.List;
import java.util.Optional;

public interface ILibraryService {
    List<Library> findAll();

    Optional<Library> findById(Long id);

    Library save(Library library);

    void updateStatus(Long id, UpdateStatusDTO updateStatusDTO);
}

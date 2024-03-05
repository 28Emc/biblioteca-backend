package com.emc.bibliotecamscore.services;

import com.emc.bibliotecamscore.models.dtos.UpdateStatusDTO;
import com.emc.bibliotecamscore.models.entities.Library;

import java.util.List;
import java.util.Optional;

public interface ILibraryService {
    List<Library> findAll();

    Optional<Library> findById(Long id);

    Library save(Library library);

    void updateStatus(Long id, UpdateStatusDTO updateStatusDTO);
}

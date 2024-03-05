package com.emc.bibliotecamscore.services;

import com.emc.bibliotecamscore.models.dtos.UpdateStatusDTO;
import com.emc.bibliotecamscore.models.entities.Library;
import com.emc.bibliotecamscore.repositories.ILibraryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LibraryServiceImpl implements ILibraryService {

    private final ILibraryRepository libraryRepository;

    public LibraryServiceImpl(ILibraryRepository libraryRepository) {
        this.libraryRepository = libraryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Library> findAll() {
        return libraryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Library> findById(Long id) {
        return libraryRepository.findById(id);
    }

    @Override
    @Transactional
    public Library save(Library library) {
        return libraryRepository.save(library);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, UpdateStatusDTO updateStatusDTO) {
        Library library = findById(id).orElseThrow();
        library.setStatus(updateStatusDTO.getStatus());
        libraryRepository.save(library);
    }
}

package com.biblioteca.backend.repositories;

import com.biblioteca.backend.models.entities.Library;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ILibraryRepository extends JpaRepository<Library, Long> {
}
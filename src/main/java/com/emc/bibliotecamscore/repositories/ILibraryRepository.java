package com.emc.bibliotecamscore.repositories;

import com.emc.bibliotecamscore.models.entities.Library;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ILibraryRepository extends JpaRepository<Library, Long> {
}
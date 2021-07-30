package com.biblioteca.backend.repository;

import com.biblioteca.backend.model.Persona.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {

    Optional<Persona> findByNroDocumento(String nroDocumento);
}

package com.biblioteca.backend.repository.security;

import java.util.Optional;
import com.biblioteca.backend.model.Empresa.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    
    Optional<Empresa> findByRuc(String ruc);

}
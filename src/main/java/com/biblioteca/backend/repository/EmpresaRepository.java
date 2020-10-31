package com.biblioteca.backend.repository;

import java.util.Optional;
import com.biblioteca.backend.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    
    public Optional<Empresa> findByRucAndIsActivo(String ruc, boolean isActivo);

    //public Optional<Empresa> fetchByIdWithLocales(Long id);

}
package com.biblioteca.backend.repository;

import java.util.List;
import java.util.Optional;
import com.biblioteca.backend.model.Empresa;
import com.biblioteca.backend.model.Local.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LocalRepository extends JpaRepository<Local, Long> {

    public Optional<Local> findById(Long id);

    public Optional<Local> findByDireccion(String direccion);

    @Query("select l from Local l join fetch l.usuarios e join fetch l.empresa em where em.id=?1 and e.id=?2")
    public Optional<Local> fetchByIdWithEmpresaAndUsuario(Long idEmpresa, Long idUsuario);

    public List<Local> findByEmpresa(Empresa empresa);

    public boolean existsByDireccion(String direccion);

}
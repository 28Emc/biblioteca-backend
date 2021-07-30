package com.biblioteca.backend.repository.core;

import com.biblioteca.backend.model.Empleado.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    Empleado findByIdUsuario(Long idUsuario);

    //@Query("select e from Empleado e join fetch e.local l where l.id=?1")
    //List<Empleado> findByLocal(Long id);
}

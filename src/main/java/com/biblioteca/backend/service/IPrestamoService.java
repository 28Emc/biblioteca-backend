package com.biblioteca.backend.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.biblioteca.backend.model.Prestamo.Prestamo;
import com.biblioteca.backend.model.Usuario.Usuario;

public interface IPrestamoService {

    Optional<Prestamo> findById(Long id);

    List<Prestamo> findAll();

    /*List<Prestamo> fetchWithLibroWithUsuarioWithEmpleado();

    List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleado(Long idLocal);

    List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleadoPerEmpleado(Long idEmpleado);

    List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleadoPerLibro(Long idLibro);

    List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleadoPerLibroAndLocal(Long idLibro, Long idLocal);

    List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleadoPerUser(Long id);

    List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleadoPerUserAll(Long id);

    List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleadoPerUserPendientes(Long id);*/

    Prestamo save(Prestamo prestamo);

    void delete(Long id);

    String mostrarFechaAmigable(LocalDateTime fecha);

}
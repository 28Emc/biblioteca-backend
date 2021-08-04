package com.biblioteca.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.biblioteca.backend.model.Prestamo.DTO.PrestamoDTO;
import com.biblioteca.backend.model.Prestamo.Prestamo;
import com.biblioteca.backend.model.Usuario.Usuario;

public interface IPrestamoService {

    Optional<Prestamo> findById(Long id);

    List<Prestamo> findAll();

    List<Prestamo> findAllByAdmin(Usuario usuarioLogueado);

    /*List<Prestamo> fetchWithLibroWithUsuarioWithEmpleado();

    List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleado(Long idLocal);

    List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleadoPerEmpleado(Long idEmpleado);

    List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleadoPerLibro(Long idLibro);

    List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleadoPerLibroAndLocal(Long idLibro, Long idLocal);

    List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleadoPerUser(Long id);

    List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleadoPerUserAll(Long id);

    List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleadoPerUserPendientes(Long id);*/

    Prestamo saveFromUser(PrestamoDTO prestamoDTO, Usuario usuarioLogueado) throws Exception;

    Prestamo save(Prestamo prestamo);

    Prestamo update(Long id, PrestamoDTO prestamoDTO, String estado) throws Exception;

    void delete(Long id);

    String mostrarFechaAmigable(LocalDateTime fecha);

}
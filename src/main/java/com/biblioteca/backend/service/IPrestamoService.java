package com.biblioteca.backend.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.biblioteca.backend.model.Prestamo.Prestamo;
import com.biblioteca.backend.model.Usuario.Usuario;

public interface IPrestamoService {

    public Optional<Prestamo> findById(Long id);

    public List<Prestamo> listarPrestamosPorRol(Map<String, Object> response, List<Prestamo> prestamos,
            Usuario usuarioLogueado);

    public List<Prestamo> fetchWithLibroWithUsuarioWithEmpleado();

    public List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleado(Long idLocal);

    public List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleadoPerEmpleado(Long idEmpleado);

    public List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleadoPerLibro(Long idLibro);

    public List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleadoPerLibroAndLocal(Long idLibro, Long idLocal);

    public List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleadoPerUser(Long id);

    public List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleadoPerUserAll(Long id);

    public List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleadoPerUserPendientes(Long id);

    public Prestamo save(Prestamo prestamo);

    public void delete(Long id);

    public String mostrarFechaAmigable(Date fecha);

}
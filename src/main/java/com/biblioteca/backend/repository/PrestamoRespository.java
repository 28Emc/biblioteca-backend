package com.biblioteca.backend.repository;

import java.util.List;
import com.biblioteca.backend.model.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PrestamoRespository extends JpaRepository<Prestamo, Long> {

    @Query("select p from Prestamo p join fetch p.usuario pe join fetch p.libro li join fetch p.empleado em")
    public List<Prestamo> fetchWithLibroWithUsuarioWithEmpleado();

    // TODOS LOS PRESTAMOS CON LIBROS, USUARIOS Y EMPLEADOS FILTRADOS POR LOCAL DEL
    // ADMIN
    @Query("select p from Prestamo p join fetch p.usuario pe join fetch p.libro li join fetch p.empleado em where em.local.id=?1")
    public List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleado(Long idLocal);

    // PRESTAMOS CON LIBROS, USUARIOS Y EMPLEADOS FILTRADOS POR ID_EMPLEADO
    // SE INCLUYE EL EMPLEADO DE PRUEBA (ID 1)
    @Query("select p from Prestamo p join fetch p.usuario pe join fetch p.libro li join fetch p.empleado em where em.id=?1 or em.id=1")
    public List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleadoPerEmpleado(Long idEmpleado);

    // PRESTAMOS CON LIBROS, USUARIOS Y EMPLEADOS FILTRADOS POR ID_LIBRO
    @Query("select p from Prestamo p join fetch p.usuario pe join fetch p.libro li join fetch p.empleado em where li.id=?1")
    public List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleadoPerLibro(Long idLibro);

    // PRESTAMOS CON LIBROS, USUARIOS Y EMPLEADOS FILTRADOS POR ID_LOCAL E ID_LIBRO
    @Query("select p from Prestamo p join fetch p.usuario pe join fetch p.libro li join fetch p.empleado em where li.id=?1 and li.local.id=?2")
    public List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleadoPerLibroAndLocal(Long idLibro, Long idLocal);

    // PRESTAMOS CON LIBROS, USUARIOS Y EMPLEADOS FILTRADOS POR ID_LIBRO
    // @Query("select p from Prestamo p join fetch p.libro li where li.id like ?1")
    // public List<Prestamo> fetchWithLibroWithUsuarioWithEmpleado(Long idLibro);

    // PRESTAMOS COMPLETADOS O ANULADOS CON LIBROS, USUARIOS Y EMPLEADOS FILTRADOS
    // POR ID_USUARIO
    // USADO POR EL USUARIO COMO "HISTORIAL DE COMPLETADOS"
    @Query("select p from Prestamo p join fetch p.usuario pu join fetch p.libro pli join fetch p.empleado pem where pu.id=?1 and p.isActivo=1")
    public List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleadoPerUser(Long id);

    // PRESTAMOS PENDIENTES POR ID_USUARIO
    // USADO POR EL USUARIO COMO "HISTORIAL DE PENDIENTES"
    @Query("select p from Prestamo p join fetch p.usuario pu join fetch p.libro pli join fetch p.empleado pem where pu.id=?1 and pem.usuario like '%Prueba%' and p.isActivo=0")
    public List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleadoPerUserPendientes(Long id);

    // PRESTAMOS TOTALES CON LIBROS, USUARIOS Y EMPLEADOS FILTRADOS
    // POR ID_USUARIO
    @Query("select p from Prestamo p join fetch p.usuario pu join fetch p.libro pli join fetch p.empleado pem where pu.id=?1")
    public List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleadoPerUserAll(Long id);

}
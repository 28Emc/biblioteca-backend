package com.biblioteca.backend.repository.core;

import java.util.List;
import java.util.Optional;

import com.biblioteca.backend.model.Libro.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {

    // USADO
    // MUESTRO SOLO LOS LIBROS SIN REPETIRSE
    @Query(value = "select * from libros group by titulo", nativeQuery = true)
    // TODO: REVISAR
    List<Libro> findByTituloGroup();

    // USADO
    // MUESTRO SOLO LOS LIBROS NO REPETIDOS PARA LA BUSQUEDA EN EL REPORTE DE
    // PRÉSTAMOS
    @Query("select l from Libro l join fetch l.local ll where l.titulo like ?1")
    // TODO: REVISAR
    List<Libro> findByTituloDistinct(String term);

    @Query("select l from Libro l where l.categoria.nombre like?1")
    // TODO: REVISAR
    List<Libro> findByCategoria(String categoria);

    @Query("select l from Libro l join fetch l.categoria lc join fetch l.local ll")
    // TODO: REVISAR
    List<Libro> fetchWithCategoriaWithLocal();

    /*@Query("select l from Libro l join fetch l.local lo join fetch lo.usuarios em where lo.id=?1 and em.id=?2")
    public List<Libro> fetchByIdWithLocalesAndEmpleado(Long idLocal, Long idEmpleado);*/

}
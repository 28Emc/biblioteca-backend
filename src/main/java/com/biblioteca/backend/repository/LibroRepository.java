package com.biblioteca.backend.repository;

import java.util.List;
import java.util.Optional;

import com.biblioteca.backend.model.Libro.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    // USADO
    // MUESTRO SOLO LOS LIBROS SIN REPETIRSE
    @Query(value = "select * from libros group by titulo", nativeQuery = true)
    public List<Libro> findByTituloGroup();

    // USADO
    // MUESTRO SOLO LOS LIBROS NO REPETIDOS PARA LA BUSQUEDA EN EL REPORTE DE
    // PRÉSTAMOS
    @Query("select l from Libro l join fetch l.local ll where l.titulo like ?1")
    public List<Libro> findByTituloDistinct(String term);

    // USADO
    @Query("select l from Libro l join fetch l.local ll where ll.id=?1")
    public List<Libro> findByLocal(Long idLocal);

    // USADO
    public List<Libro> findByIsActivo(boolean isActivo);

    // USADO
    @Query("select l from Libro l where l.categoria.nombre like?1")
    public List<Libro> findByCategoria(String categoria);

    // USADO
    @Query("select l from Libro l join fetch l.local ll where ll.id=?1 and l.isActivo=?2")
    public List<Libro> findByLocalAndIsActivo(Long idLocal, boolean isActivo);

    // USADO
    @Query("select l from Libro l join fetch l.categoria lc join fetch l.local ll where lc.nombre like ?1 and ll.id=?2")
    public List<Libro> findByCategoriaAndLocal(String categoria, Long idLocal);

    // USADO
    @Query("select l from Libro l join fetch l.local lo where l.titulo like ?1")
    public Optional<Libro> findByTituloLikeIgnoreCase(String titulo);

    // USADO
    @Query("select l from Libro l join fetch l.local lo where l.titulo like ?1 and lo.id = ?2 and l.isActivo = ?3")
    public List<Libro> findByTituloLikeIgnoreCaseAndLocalAndIsActivo(String term, Long id, Boolean isActivo);

    // USADO
    @Query("select l from Libro l join fetch l.local lo where l.titulo like ?1 and lo.id = ?2")
    public Optional<Libro> findByTituloAndLocal(String term, Long id);

    // USADO
    @Query("select l from Libro l join fetch l.local lo where l.titulo like ?1 and lo.id = ?2 and l.isActivo = ?3")
    public Optional<Libro> findByTituloAndLocalAndIsActivo(String term, Long id, Boolean isActivo);

    // USADO
    @Query("select l from Libro l join fetch l.categoria lc join fetch l.local ll")
    public List<Libro> fetchWithCategoriaWithLocal();

    @Query("select l from Libro l join fetch l.local lo where lo.id=?1")
    public List<Libro> fetchByIdWithLocales(Long idLocal);

    @Query("select l from Libro l join fetch l.local lo join fetch lo.usuarios em where lo.id=?1 and em.id=?2")
    public List<Libro> fetchByIdWithLocalesAndEmpleado(Long idLocal, Long idEmpleado);

}
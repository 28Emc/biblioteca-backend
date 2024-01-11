package com.biblioteca.backend.repositories;

import com.biblioteca.backend.models.entities.Book;
import com.biblioteca.backend.models.projections.BookView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IBookRepository extends JpaRepository<Book, Long> {
    // INTERFACE BASED PROJECTION (OPENED)
    @Query(value = "SELECT * FROM tb_book b INNER JOIN tb_library l ON l.id = b.library_id " +
            "INNER JOIN tb_category c ON c.id = b.category_id;", nativeQuery = true)
    List<BookView> findAllWithView();

    @Query(value = "SELECT * FROM tb_book b INNER JOIN tb_library l ON l.id = b.library_id " +
            "INNER JOIN tb_category c ON c.id = b.category_id WHERE b.id = :id", nativeQuery = true)
    Optional<BookView> findByIdWithView(Long id);

    List<Book> findByLibraryId(Long libraryId);

    // INTERFACE BASED PROJECTION (CLOSED)
    @Query(value = "SELECT * FROM tb_book b INNER JOIN tb_library l ON l.id = b.library_id " +
            "INNER JOIN tb_category c ON c.id = b.category_id WHERE l.id = :libraryId", nativeQuery = true)
    List<BookView> findByLibraryIdWithView(Long libraryId);

    List<Book> findByCategoryId(Long categoryId);

    @Query(value = "SELECT * FROM tb_book b INNER JOIN tb_library l ON l.id = b.library_id " +
            "INNER JOIN tb_category c ON c.id = b.category_id WHERE c.id = :categoryId", nativeQuery = true)
    List<BookView> findByCategoryIdWithView(Long categoryId);

    Optional<Book> findByLibraryIdAndISBN(Long libraryId, String isbn);

    @Query(value = "SELECT * FROM tb_book b INNER JOIN tb_library l ON l.id = b.library_id " +
            "INNER JOIN tb_category c ON c.id = b.category_id WHERE l.id = :libraryId AND b.isbn = :isbn",
            nativeQuery = true)
    Optional<BookView> findByLibraryIdAndISBNWithView(Long libraryId, String isbn);

    Optional<Book> findByTitleIgnoreCase(String title);
}
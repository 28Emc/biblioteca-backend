package com.biblioteca.backend.repositories;

import com.biblioteca.backend.models.entities.BookCopy;
import com.biblioteca.backend.models.projections.BookCopyView;
import com.biblioteca.backend.models.projections.BookView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IBookCopyRepository extends JpaRepository<BookCopy, Long> {

    // INTERFACE BASED PROJECTION (OPENED)
    @Query(value = "SELECT bc.id as book_copy_id, b.id as book_id, l.id as library_id, c.id as category_id, " +
            "bc.*, b.*, l.*, c.* FROM tb_book_copy bc INNER JOIN tb_book b ON b.id = bc.book_id " +
            "INNER JOIN tb_library l ON l.id = b.library_id INNER JOIN tb_category c ON c.id = b.category_id;",
            nativeQuery = true)
    List<BookCopyView> findAllWithView();

    @Query(value = "SELECT bc.id as book_copy_id, b.id as book_id, l.id as library_id, c.id as category_id, " +
            "bc.*, b.*, l.*, c.* FROM tb_book_copy bc INNER JOIN tb_book b ON b.id = bc.book_id " +
            "INNER JOIN tb_library l ON l.id = b.library_id INNER JOIN tb_category c ON c.id = b.category_id " +
            "WHERE bc.id = :book_copy_id",
            nativeQuery = true)
    Optional<BookCopyView> getOneByIdWithView(@Param(value = "book_copy_id") Long bookCopyId);

    List<BookCopy> findByISBN(String isbn);

    List<BookCopy> findByStatus(String status);
}
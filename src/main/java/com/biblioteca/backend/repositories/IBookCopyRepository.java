package com.biblioteca.backend.repositories;

import com.biblioteca.backend.models.entities.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IBookCopyRepository extends JpaRepository<BookCopy, Long> {
    List<BookCopy> findByISBN(String isbn);
    List<BookCopy> findByStatus(String status);

}
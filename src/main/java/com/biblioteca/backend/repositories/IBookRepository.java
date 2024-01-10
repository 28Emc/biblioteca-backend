package com.biblioteca.backend.repositories;

import com.biblioteca.backend.models.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IBookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByISBN(String isbn);
    Optional<Book> findByTitleIgnoreCase(String title);
}
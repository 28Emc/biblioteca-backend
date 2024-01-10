package com.biblioteca.backend.services;

import com.biblioteca.backend.models.dtos.BookDTO;
import com.biblioteca.backend.models.entities.Book;

import java.util.List;
import java.util.Optional;

public interface IBookService {
    List<Book> findAll();

    Optional<Book> findById(Long id);

    Optional<Book> findByISBN(String isbn);

    Optional<Book> findByTitle(String title);

    void save(BookDTO bookDTO);

    void updateStatus(Book book);
}

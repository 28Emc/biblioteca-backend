package com.biblioteca.backend.services;

import com.biblioteca.backend.models.entities.Book;

import java.util.List;
import java.util.Optional;

public interface IBookService {
    List<Book> findAll();

    Optional<Book> findById(Long id);

    Optional<Book> findByTitle(String title);

    Book save(Book book);
}

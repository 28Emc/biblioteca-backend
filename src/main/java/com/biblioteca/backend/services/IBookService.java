package com.biblioteca.backend.services;

import com.biblioteca.backend.models.entities.Book;

import java.util.List;
import java.util.Optional;

public interface IBookService {
    public List<Book> findAll();

    public Optional<Book> findById(Long id);

    public Optional<Book> findByTitle(String title);

    public Book save(Book book);
}

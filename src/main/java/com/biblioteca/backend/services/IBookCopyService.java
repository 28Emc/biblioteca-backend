package com.biblioteca.backend.services;

import com.biblioteca.backend.models.dtos.BookCopyDTO;
import com.biblioteca.backend.models.entities.Book;
import com.biblioteca.backend.models.entities.BookCopy;
import com.biblioteca.backend.models.projections.BookCopyView;

import java.util.List;
import java.util.Optional;

public interface IBookCopyService {
    List<BookCopy> findAll();

    List<BookCopyView> findAllWithView();

    List<BookCopy> findByISBN(String isbn);

    List<BookCopy> findByStatus(String status);

    Optional<BookCopy> findById(Long id);

    Optional<BookCopyView> getOneByIdWithView(Long bookCopyId);

    void save(Book book, Integer quantity);

    void updateStatus(Long id, String status);

    void delete(Long id);
}

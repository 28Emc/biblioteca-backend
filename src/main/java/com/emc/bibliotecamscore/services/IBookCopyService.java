package com.emc.bibliotecamscore.services;

import com.emc.bibliotecamscore.models.dtos.UpdateStatusDTO;
import com.emc.bibliotecamscore.models.entities.Book;
import com.emc.bibliotecamscore.models.entities.BookCopy;
import com.emc.bibliotecamscore.models.projections.BookCopyView;

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

    void updateStatus(Long id, UpdateStatusDTO updateStatusDTO);

    void delete(Long id);
}

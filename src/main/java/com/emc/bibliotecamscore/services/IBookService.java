package com.emc.bibliotecamscore.services;

import com.emc.bibliotecamscore.models.dtos.BookDTO;
import com.emc.bibliotecamscore.models.dtos.UpdateStatusDTO;
import com.emc.bibliotecamscore.models.entities.Book;
import com.emc.bibliotecamscore.models.projections.BookView;

import java.util.List;
import java.util.Optional;

public interface IBookService {
    List<Book> findAll();

    List<BookView> findAllWithView();

    List<Book> findByLibraryId(Long libraryId);

    List<BookView> findByLibraryIdWithView(Long libraryId);

    List<Book> findByCategoryId(Long categoryId);

    List<BookView> findByCategoryIdWithView(Long categoryId);

    Optional<Book> findById(Long id);

    Optional<BookView> findByIdWithView(Long id);

    Optional<Book> findByLibraryIdAndISBN(Long libraryId, String isbn);

    Optional<BookView> findByLibraryIdAndISBNWithView(Long libraryId, String isbn);

    Optional<Book> findByTitle(String title);

    void save(BookDTO bookDTO);

    void updateStatus(Long id, UpdateStatusDTO updateStatusDTO);
}

package com.emc.bibliotecamscore.services;

import com.emc.bibliotecamscore.models.dtos.UpdateStatusDTO;
import com.emc.bibliotecamscore.models.entities.Book;
import com.emc.bibliotecamscore.models.entities.BookCopy;
import com.emc.bibliotecamscore.models.projections.BookCopyView;
import com.emc.bibliotecamscore.repositories.IBookCopyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookCopyServiceImpl implements IBookCopyService {

    private final IBookCopyRepository bookCopyRepository;

    public BookCopyServiceImpl(IBookCopyRepository bookCopyRepository) {
        this.bookCopyRepository = bookCopyRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookCopy> findAll() {
        return bookCopyRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookCopyView> findAllWithView() {
        return bookCopyRepository.findAllWithView();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookCopy> findByISBN(String isbn) {
        return bookCopyRepository.findByISBN(isbn);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookCopy> findByStatus(String status) {
        return bookCopyRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookCopy> findById(Long id) {
        return bookCopyRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookCopyView> getOneByIdWithView(Long bookCopyId) {
        return bookCopyRepository.getOneByIdWithView(bookCopyId);
    }

    @Override
    @Transactional
    public void save(Book book, Integer quantity) {
        List<BookCopy> bookCopyList = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            BookCopy bookCopy = new BookCopy();
            bookCopy.setBook(book);
            bookCopy.setISBN(book.getISBN());
            bookCopyList.add(bookCopy);
        }
        bookCopyRepository.saveAll(bookCopyList);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, UpdateStatusDTO updateStatusDTO) {
        BookCopy bookCopy = findById(id).orElseThrow();
        bookCopy.setStatus(updateStatusDTO.getStatus());
        bookCopyRepository.save(bookCopy);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        bookCopyRepository.deleteById(id);
    }
}

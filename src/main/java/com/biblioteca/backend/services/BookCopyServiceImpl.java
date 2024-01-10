package com.biblioteca.backend.services;

import com.biblioteca.backend.models.entities.Book;
import com.biblioteca.backend.models.entities.BookCopy;
import com.biblioteca.backend.repositories.IBookCopyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public void save(Book book) {
        BookCopy bookCopy = new BookCopy();
        bookCopy.setBook(book);
        bookCopy.setISBN(book.getISBN());
        bookCopyRepository.save(bookCopy);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, String status) {
        BookCopy bookCopy = bookCopyRepository.findById(id).orElseThrow();
        bookCopy.setStatus(status);
        bookCopyRepository.save(bookCopy);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        bookCopyRepository.deleteById(id);
    }
}

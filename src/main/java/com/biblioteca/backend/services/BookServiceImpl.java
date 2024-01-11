package com.biblioteca.backend.services;

import com.biblioteca.backend.models.dtos.BookDTO;
import com.biblioteca.backend.models.dtos.UpdateStatusDTO;
import com.biblioteca.backend.models.entities.Book;
import com.biblioteca.backend.models.entities.BookCopy;
import com.biblioteca.backend.models.projections.BookView;
import com.biblioteca.backend.repositories.IBookCopyRepository;
import com.biblioteca.backend.repositories.IBookRepository;
import com.biblioteca.backend.repositories.ICategoryRepository;
import com.biblioteca.backend.repositories.ILibraryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements IBookService {

    private final ILibraryRepository libraryRepository;
    private final ICategoryRepository categoryRepository;
    private final IBookRepository bookRepository;
    private final IBookCopyRepository bookCopyRepository;

    public BookServiceImpl(ILibraryRepository libraryRepository, ICategoryRepository categoryRepository,
                           IBookRepository bookRepository, IBookCopyRepository bookCopyRepository) {
        this.libraryRepository = libraryRepository;
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
        this.bookCopyRepository = bookCopyRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookView> findAllWithView() {
        return bookRepository.findAllWithView();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findByLibraryId(Long libraryId) {
        return bookRepository.findByLibraryId(libraryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookView> findByLibraryIdWithView(Long libraryId) {
        return bookRepository.findByLibraryIdWithView(libraryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findByCategoryId(Long categoryId) {
        return bookRepository.findByCategoryId(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookView> findByCategoryIdWithView(Long categoryId) {
        return bookRepository.findByCategoryIdWithView(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookView> findByIdWithView(Long id) {
        return bookRepository.findByIdWithView(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findByLibraryIdAndISBN(Long libraryId, String isbn) {
        return bookRepository.findByLibraryIdAndISBN(libraryId, isbn);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookView> findByLibraryIdAndISBNWithView(Long libraryId, String isbn) {
        return bookRepository.findByLibraryIdAndISBNWithView(libraryId, isbn);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findByTitle(String title) {
        return bookRepository.findByTitleIgnoreCase(title);
    }

    @Override
    @Transactional
    public void save(BookDTO bookDTO) {
        Book book = new Book();
        book.setLibrary(libraryRepository.findById(bookDTO.getLibraryId().longValue()).orElseThrow());
        book.setCategory(categoryRepository.findById(bookDTO.getCategoryId().longValue()).orElseThrow());
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setPublishingHouse(bookDTO.getPublishingHouse());
        book.setSynopsis(bookDTO.getSynopsis());
        book.setPublishDate(LocalDate.parse(bookDTO.getPublishDate()));
        book.setStock(bookDTO.getStock());
        book.setImage(bookDTO.getImage());
        bookRepository.save(book);
        Book createdBook = bookRepository.save(book);
        if (bookDTO.getId() == null) {
            BookCopy bookCopy = new BookCopy();
            bookCopy.setBook(createdBook);
            bookCopy.setISBN(createdBook.getISBN());
            bookCopyRepository.save(bookCopy);
        }
    }

    @Override
    @Transactional
    public void updateStatus(Long id, UpdateStatusDTO updateStatusDTO) {
        Book book = findById(id).orElseThrow();
        book.setStatus(updateStatusDTO.getStatus());
        bookRepository.save(book);
    }
}

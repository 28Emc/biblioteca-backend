package com.biblioteca.backend.services;

import com.biblioteca.backend.models.dtos.BookLoanDTO;
import com.biblioteca.backend.models.dtos.UpdateStatusDTO;
import com.biblioteca.backend.models.entities.Book;
import com.biblioteca.backend.models.entities.BookCopy;
import com.biblioteca.backend.models.entities.BookLoan;
import com.biblioteca.backend.models.entities.Member;
import com.biblioteca.backend.models.projections.BookLoanView;
import com.biblioteca.backend.repositories.IBookCopyRepository;
import com.biblioteca.backend.repositories.IBookLoanRepository;
import com.biblioteca.backend.repositories.IBookRepository;
import com.biblioteca.backend.repositories.IMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class BookLoanServiceImpl implements IBookLoanService {

    private final IBookLoanRepository bookLoanRepository;

    private final IMemberRepository memberRepository;

    private final IBookRepository bookRepository;

    private final IBookCopyRepository bookCopyRepository;

    public BookLoanServiceImpl(IBookLoanRepository bookLoanRepository,
                               IMemberRepository memberRepository,
                               IBookRepository bookRepository,
                               IBookCopyRepository bookCopyRepository) {
        this.bookLoanRepository = bookLoanRepository;
        this.memberRepository = memberRepository;
        this.bookRepository = bookRepository;
        this.bookCopyRepository = bookCopyRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookLoan> findAll() {
        return bookLoanRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookLoanView> findAllWithView() {
        return bookLoanRepository.findAllWithView();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookLoan> findById(Long id) {
        return bookLoanRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookLoanView> findByIdWithView(Long id) {
        return bookLoanRepository.findByIdWithView(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookLoan> findByCode(String code) {
        return bookLoanRepository.findByCode(code);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookLoanView> findByCodeWithView(String code) {
        return bookLoanRepository.findByCodeWithView(code);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookLoan> findByStatus(String status) {
        return bookLoanRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookLoanView> findByStatusWithView(String status) {
        return bookLoanRepository.findByStatusWithView(status);
    }

    @Override
    @Transactional
    public BookLoan save(BookLoanDTO bookLoanDTO, Long id) {
        Book bookFound = bookRepository.findById(bookLoanDTO.getBookId())
                .orElseThrow(() -> new NoSuchElementException("Book not found"));
        Member memberFound = memberRepository.findById(bookLoanDTO.getMemberId())
                .orElseThrow(() -> new NoSuchElementException("Member not found"));
        BookLoan bookLoan = new BookLoan();
        if (id != null) {
            bookLoan = findById(id).orElseThrow(() -> new NoSuchElementException("Book loan not found"));
        } else {
            BookCopy bookCopyFound = bookCopyRepository.findByISBN(bookFound.getISBN())
                    .stream()
                    .filter(b -> b.getStatus().equals("D"))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("No book copy available at the moment"));
            bookCopyFound.setStatus("P");
            bookCopyRepository.save(bookCopyFound);
        }
        bookLoan.setLoanDate(LocalDate.parse(bookLoanDTO.getLoanDate()));
        bookLoan.setReturnDate(LocalDate.parse(bookLoanDTO.getReturnDate()));
        bookLoan.setMember(memberFound);
        // bookLoan.setEmployee(employeeRepository.findById(bookLoanDTO.getEmployeeId()).orElseThrow());
        bookLoan.setBook(bookFound);
        // TODO: ADD OPERATION LOG LOGIC
        return bookLoanRepository.save(bookLoan);
    }

    @Override
    @Transactional
    public String updateStatus(Long id, UpdateStatusDTO updateStatusDTO) {
        BookLoan bookLoanFound = findById(id)
                .orElseThrow(() -> new NoSuchElementException("Book loan not found"));
        Book bookFound = bookRepository.findById(bookLoanFound.getBook().getId())
                .orElseThrow(() -> new NoSuchElementException("Book not found"));
        String msg;
        switch (updateStatusDTO.getStatus()) {
            case "A" -> { // BOOK_LOAN STATUS "ACTIVE" => BOOK_COPY STATUS FROM "PENDING" TO "ACTIVE"
                BookCopy bookCopyFound = bookCopyRepository.findByISBN(bookFound.getISBN())
                        .stream()
                        .filter(b -> b.getStatus().equals("P"))
                        .findFirst()
                        .orElseThrow(() -> new NoSuchElementException("No book copy with PENDING status found"));
                bookCopyFound.setStatus("A"); // BOOK COPY STATUS IS "ACTIVE"
                bookCopyRepository.save(bookCopyFound);
                msg = "Book loan confirmed";
            }
            case "O" -> { // BOOK_LOAN STATUS "OVERDUE" => BOOK_COPY STATUS FROM "ACTIVE" TO "OVERDUE"
                BookCopy bookCopyFound = bookCopyRepository.findByISBN(bookFound.getISBN())
                        .stream()
                        .filter(b -> b.getStatus().equals("A"))
                        .findFirst()
                        .orElseThrow(() -> new NoSuchElementException("No book copy with ACTIVE status found"));
                bookCopyFound.setStatus("O"); // BOOK COPY STATUS IS "OVERDUE"
                bookCopyRepository.save(bookCopyFound);
                msg = "Book loan overdue";
            }
            case "R" -> { // BOOK_LOAN STATUS "RETURNED" => BOOK_COPY STATUS FROM "ACTIVE" OR "OVERDUE" TO "RETURNED"
                BookCopy bookCopyFound = bookCopyRepository.findByISBN(bookFound.getISBN())
                        .stream()
                        .filter(b -> b.getStatus().equals("A") || b.getStatus().equals("O"))
                        .findFirst()
                        .orElseThrow(() -> new NoSuchElementException("No book copy with ACTIVE or OVERDUE status" +
                                " found"));
                bookCopyFound.setStatus("R"); // BOOK COPY STATUS IS "RETURNED"
                bookCopyRepository.save(bookCopyFound);
                msg = "Book loan returned";
                // TODO: APPLY LOGIC TO BOOK COPY STATUS UPDATE TO "PENDING"
            }
            case "C" -> { // BOOK_LOAN STATUS "CANCELED" => BOOK_COPY STATUS REMAINS "PENDING"
                BookCopy bookCopyFound = bookCopyRepository.findByISBN(bookFound.getISBN())
                        .stream()
                        .filter(b -> b.getStatus().equals("P"))
                        .findFirst()
                        .orElseThrow(() -> new NoSuchElementException("No book copy with PENDING status found"));
                bookCopyFound.setStatus("P"); // BOOK COPY STATUS IS "PENDING"
                bookCopyRepository.save(bookCopyFound);
                msg = "Book loan canceled";
            }
            default -> { // BOOK_LOAN STATUS "PENDING" => BOOK_COPY STATUS REMAINS "PENDING"
                /*
                BookCopy bookCopyFound = bookCopyRepository.findByISBN(bookFound.getISBN())
                        .stream()
                        .filter(b -> b.getStatus().equals("P"))
                        .findFirst()
                        .orElseThrow(() -> new NoSuchElementException("No book copy with PENDING status found"));
                bookCopyFound.setStatus("P"); // BOOK COPY STATUS IS "PENDING"
                bookCopyRepository.save(bookCopyFound);
                msg = "Book loan pending";
                */
                msg = "No changes were made to the book loan";
            }
        }
        bookLoanFound.setStatus(updateStatusDTO.getStatus());
        bookLoanRepository.save(bookLoanFound);
        return msg;
    }
}

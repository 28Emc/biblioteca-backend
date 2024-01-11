package com.biblioteca.backend.services;

import com.biblioteca.backend.models.dtos.BookLoanDTO;
import com.biblioteca.backend.models.dtos.UpdateStatusDTO;
import com.biblioteca.backend.models.entities.BookLoan;
import com.biblioteca.backend.models.projections.BookLoanView;

import java.util.List;
import java.util.Optional;

public interface IBookLoanService {
    List<BookLoan> findAll();

    List<BookLoanView> findAllWithView();

    Optional<BookLoan> findById(Long id);

    Optional<BookLoanView> findByIdWithView(Long id);

    Optional<BookLoan> findByCode(String code);

    Optional<BookLoanView> findByCodeWithView(String code);

    List<BookLoan> findByStatus(String status);

    List<BookLoanView> findByStatusWithView(String status);

    BookLoan save(BookLoanDTO bookLoanDTO, Long id);

    String updateStatus(Long id, UpdateStatusDTO updateStatusDTO);
}

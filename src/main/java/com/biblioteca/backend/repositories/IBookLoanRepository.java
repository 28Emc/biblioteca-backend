package com.biblioteca.backend.repositories;

import com.biblioteca.backend.models.entities.BookLoan;
import com.biblioteca.backend.models.projections.BookLoanView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IBookLoanRepository extends JpaRepository<BookLoan, Long> {

    // INTERFACE BASED PROJECTION (OPENED)
    @Query(value = "SELECT bl.id as book_loan_id, m.id as member_id, CONCAT(m.name, ' ', m.last_name) " +
            "as member_name, m.doc_nro as member_doc_nro, b.id as book_id, b.isbn as book_isbn, b.title " +
            "as book_title, c.id as category_id, c.name as category_name, bl.code, bl.loan_date, bl.return_date, " +
            "bl.status, bl.creation_date, bl.modification_date FROM tb_book_loan bl INNER JOIN tb_book b " +
            "ON b.id = bl.book_id INNER JOIN tb_category c ON c.id = b.category_id INNER JOIN tb_member m " +
            "ON m.id = bl.member_id",
            nativeQuery = true)
    List<BookLoanView> findAllWithView();

    @Query(value = "SELECT bl.id as book_loan_id, m.id as member_id, CONCAT(m.name, ' ', m.last_name) " +
            "as member_name, m.doc_nro as member_doc_nro, b.id as book_id, b.isbn as book_isbn, b.title " +
            "as book_title, c.id as category_id, c.name as category_name, bl.code, bl.loan_date, bl.return_date, " +
            "bl.status, bl.creation_date, bl.modification_date FROM tb_book_loan bl INNER JOIN tb_book b " +
            "ON b.id = bl.book_id INNER JOIN tb_category c ON c.id = b.category_id INNER JOIN tb_member m " +
            "ON m.id = bl.member_id WHERE bl.id = :bookLoanId",
            nativeQuery = true)
    Optional<BookLoanView> findByIdWithView(Long bookLoanId);

    Optional<BookLoan> findByCode(String code);

    @Query(value = "SELECT bl.id as book_loan_id, m.id as member_id, CONCAT(m.name, ' ', m.last_name) " +
            "as member_name, m.doc_nro as member_doc_nro, b.id as book_id, b.isbn as book_isbn, b.title " +
            "as book_title, c.id as category_id, c.name as category_name, bl.code, bl.loan_date, bl.return_date, " +
            "bl.status, bl.creation_date, bl.modification_date FROM tb_book_loan bl INNER JOIN tb_book b " +
            "ON b.id = bl.book_id INNER JOIN tb_category c ON c.id = b.category_id INNER JOIN tb_member m " +
            "ON m.id = bl.member_id WHERE bl.code = :bookLoanCode",
            nativeQuery = true)
    Optional<BookLoanView> findByCodeWithView(String bookLoanCode);

    List<BookLoan> findByStatus(String status);

    @Query(value = "SELECT bl.id as book_loan_id, m.id as member_id, CONCAT(m.name, ' ', m.last_name) " +
            "as member_name, m.doc_nro as member_doc_nro, b.id as book_id, b.isbn as book_isbn, b.title " +
            "as book_title, c.id as category_id, c.name as category_name, bl.code, bl.loan_date, bl.return_date, " +
            "bl.status, bl.creation_date, bl.modification_date FROM tb_book_loan bl INNER JOIN tb_book b " +
            "ON b.id = bl.book_id INNER JOIN tb_category c ON c.id = b.category_id INNER JOIN tb_member m " +
            "ON m.id = bl.member_id WHERE bl.status = :bookLoanStatus",
            nativeQuery = true)
    List<BookLoanView> findByStatusWithView(String bookLoanStatus);
}
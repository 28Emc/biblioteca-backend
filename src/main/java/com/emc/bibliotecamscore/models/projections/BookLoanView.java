package com.emc.bibliotecamscore.models.projections;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface BookLoanView {
    @Value("#{target.book_loan_id}")
    Long getBookLoanId();

    @Value("#{target.member_id}")
    Long getMemberId();

    @Value("#{target.member_name}")
    String getMemberName();

    @Value("#{target.member_doc_nro}")
    String getMemberDocNro();

    @Value("#{target.employee_id}")
    Long getEmployeeId();

    @Value("#{target.employee_name}")
    String getEmployeeName();

    @Value("#{target.book_id}")
    Long getBookId();

    @Value("#{target.book_isbn}")
    String getBookISBN();

    @Value("#{target.book_title}")
    String getBookTitle();

    @Value("#{target.category_name}")
    String getCategoryName();

    @Value("#{target.code}")
    String getCode();

    @Value("#{target.loan_date}")
    LocalDate getLoanDate();

    @Value("#{target.return_date}")
    LocalDate getReturnDate();

    @Value("#{target.status}")
    String getStatus();

    @Value("#{target.creation_date}")
    LocalDateTime getCreationDate();

    @Value("#{target.modification_date}")
    LocalDateTime getModificationDate();
}

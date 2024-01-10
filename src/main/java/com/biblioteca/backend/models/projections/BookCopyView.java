package com.biblioteca.backend.models.projections;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

public interface BookCopyView {
    @Value("#{target.book_copy_id}")
    Long getId();

    @Value("#{target.book_id}")
    Long getBookId();

    @Value("#{target.library_id}")
    Long getLibraryId();

    @Value("#{target.category_id}")
    Long getCategoryId();

    @Value("#{target.isbn}")
    String getISBN();

    @Value("#{target.status}")
    String getStatus();

    @Value("#{target.creation_date}")
    LocalDateTime getCreationDate();

    @Value("#{target.modification_date}")
    LocalDateTime getModificationDate();
}

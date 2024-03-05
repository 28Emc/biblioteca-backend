package com.emc.bibliotecamscore.models.projections;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface BookView {
    @Value("#{target.id}")
    Long getId();

    @Value("#{target.library_id}")
    Long getLibraryId();

    @Value("#{target.category_id}")
    Long getCategoryId();

    @Value("#{target.isbn}")
    String getISBN();

    @Value("#{target.title}")
    String getTitle();

    @Value("#{target.author}")
    String getAuthor();

    @Value("#{target.publishing_house}")
    String getPublishingHouse();

    @Value("#{target.synopsis}")
    String getSynopsis();

    @Value("#{target.stock}")
    Integer getStock();

    @Value("#{target.image}")
    String getImage();

    @Value("#{target.status}")
    String getStatus();

    @Value("#{target.publish_date}")
    LocalDate getPublishDate();

    @Value("#{target.creation_date}")
    LocalDateTime getCreationDate();

    @Value("#{target.modification_date}")
    LocalDateTime getModificationDate();
}

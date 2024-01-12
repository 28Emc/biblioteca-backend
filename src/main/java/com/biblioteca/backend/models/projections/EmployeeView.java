package com.biblioteca.backend.models.projections;

import org.springframework.beans.factory.annotation.Value;

public interface EmployeeView {
    @Value("#{target.employee_id}")
    Long getEmployeeId();

    @Value("#{target.library_id}")
    Long getLibraryId();

    @Value("#{target.library_address}")
    String getLibraryAddress();

    @Value("#{target.uuid}")
    String getUuid();

    @Value("#{target.name}")
    String getName();

    @Value("#{target.last_name}")
    String getLastName();

    @Value("#{target.position}")
    String getPosition();

    @Value("#{target.doc_nro}")
    String getDocNro();

    @Value("#{target.phone_number}")
    String getPhoneNumber();

    @Value("#{target.email}")
    String getEmail();

    @Value("#{target.status}")
    String getStatus();
}

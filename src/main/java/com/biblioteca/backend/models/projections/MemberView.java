package com.biblioteca.backend.models.projections;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;

public interface MemberView {
    @Value("#{target.member_id}")
    Long getMemberId();

    @Value("#{target.uuid}")
    String getUuid();

    @Value("#{target.name}")
    String getName();

    @Value("#{target.last_name}")
    String getLastName();

    @Value("#{target.doc_type}")
    String getDocType();

    @Value("#{target.doc_nro}")
    String getDocNro();

    @Value("#{target.address}")
    String getAddress();

    @Value("#{target.address_reference}")
    String getAddressReference();

    @Value("#{target.phone_number}")
    String getPhoneNumber();

    @Value("#{target.email}")
    String getEmail();

    @Value("#{target.alias}")
    String getAlias();

    @Value("#{target.status}")
    String getStatus();

    @Value("#{target.avatar_img}")
    String getAvatarImg();
}

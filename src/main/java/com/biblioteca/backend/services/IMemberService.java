package com.biblioteca.backend.services;

import com.biblioteca.backend.models.dtos.MemberDTO;
import com.biblioteca.backend.models.dtos.UpdateStatusDTO;
import com.biblioteca.backend.models.entities.Member;
import com.biblioteca.backend.models.projections.MemberView;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface IMemberService {
    List<Member> findAll();

    List<MemberView> findAllWithView();

    Optional<Member> findById(Long id);

    Optional<MemberView> findByIdWithView(Long id);

    Optional<Member> findByUuid(String uuid);

    Optional<MemberView> findByUuidWithView(String uuid);

    Optional<Member> findByDocNro(String docNro);

    Optional<MemberView> findByDocNroWithView(String docNro);

    Optional<Member> findByEmail(String email);

    Optional<MemberView> findByEmailWithView(String email);

    Optional<Member> findByPhoneNumber(String phoneNumber);

    Optional<MemberView> findByPhoneNumberWithView(String phoneNumber);

    Member save(MemberDTO memberDTO);

    void updateStatus(Long id, UpdateStatusDTO updateStatusDTO);
}

package com.biblioteca.backend.repositories;

import com.biblioteca.backend.models.entities.Member;
import com.biblioteca.backend.models.projections.MemberView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IMemberRepository extends JpaRepository<Member, Long> {

    @Query(value = "SELECT m.id as member_id, m.* FROM tb_member m", nativeQuery = true)
    List<MemberView> findAllWithView();
    @Query(value = "SELECT m.id as member_id, m.* FROM tb_member m WHERE m.id = :id", nativeQuery = true)
    Optional<MemberView> findByIdWithView(Long id);

    Optional<Member> findByUuid(String uuid);

    @Query(value = "SELECT m.id as member_id, m.* FROM tb_member m WHERE m.uuid = :uuid", nativeQuery = true)
    Optional<MemberView> findByUuidWithView(String uuid);

    Optional<Member> findByDocNro(String docNro);

    @Query(value = "SELECT m.id as member_id, m.* FROM tb_member m WHERE m.doc_nro = :docNro", nativeQuery = true)
    Optional<MemberView> findByDocNroWithView(String docNro);

    Optional<Member> findByEmail(String email);

    @Query(value = "SELECT m.id as member_id, m.* FROM tb_member m WHERE m.email = :email", nativeQuery = true)
    Optional<MemberView> findByEmailWithView(String email);

    Optional<Member> findByPhoneNumber(String phoneNumber);

    @Query(value = "SELECT m.id as member_id, m.* FROM tb_member m WHERE m.phone_number = :phoneNumber",
            nativeQuery = true)
    Optional<MemberView> findByPhoneNumberWithView(String phoneNumber);
}
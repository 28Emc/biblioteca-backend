package com.biblioteca.backend.services;

import com.biblioteca.backend.models.dtos.MemberDTO;
import com.biblioteca.backend.models.dtos.UpdateStatusDTO;
import com.biblioteca.backend.models.entities.Member;
import com.biblioteca.backend.models.projections.MemberView;
import com.biblioteca.backend.repositories.IMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MemberServiceImpl implements IMemberService {

    private final IMemberRepository memberRepository;

    public MemberServiceImpl(IMemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberView> findAllWithView() {
        return memberRepository.findAllWithView();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MemberView> findByIdWithView(Long id) {
        return memberRepository.findByIdWithView(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Member> findByUuid(String uuid) {
        return memberRepository.findByUuid(uuid);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MemberView> findByUuidWithView(String uuid) {
        return memberRepository.findByUuidWithView(uuid);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Member> findByDocNro(String docNro) {
        return memberRepository.findByDocNro(docNro);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MemberView> findByDocNroWithView(String docNro) {
        return memberRepository.findByDocNroWithView(docNro);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MemberView> findByEmailWithView(String email) {
        return memberRepository.findByEmailWithView(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Member> findByPhoneNumber(String phoneNumber) {
        return memberRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MemberView> findByPhoneNumberWithView(String phoneNumber) {
        return memberRepository.findByPhoneNumberWithView(phoneNumber);
    }

    @Override
    @Transactional
    public Member save(MemberDTO memberDTO) {
        Member member = new Member();
        member.setId(memberDTO.getId());
        member.setUuid(memberDTO.getUuid());
        member.setName(memberDTO.getName());
        member.setLastName(memberDTO.getLastName());
        member.setDocType(memberDTO.getDocType());
        member.setDocNro(memberDTO.getDocNro());
        member.setAddress(memberDTO.getAddress());
        member.setAddressReference(memberDTO.getAddressReference());
        member.setPhoneNumber(memberDTO.getPhoneNumber());
        member.setEmail(memberDTO.getEmail());
        member.setAlias(memberDTO.getAlias());
        member.setAvatarImg(memberDTO.getAvatarImg());
        member.setStatus(memberDTO.getStatus());
        member.setCreationDate(memberDTO.getCreationDate());
        return memberRepository.save(member);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, UpdateStatusDTO updateStatusDTO) {
        Member member = findById(id).orElseThrow();
        member.setStatus(updateStatusDTO.getStatus());
        memberRepository.save(member);
    }
}

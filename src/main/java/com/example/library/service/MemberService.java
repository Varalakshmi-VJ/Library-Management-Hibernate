package com.example.library.service;

import com.example.library.model.Member;
import com.example.library.repository.MemberRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public List<Member> listWithIssues() {
        return memberRepository.findAllWithIssues();
    }

    @Transactional(readOnly = true)
    public List<Member> all() {
        return memberRepository.findAll();
    }

    public boolean emailTaken(String email) {
        return memberRepository.existsByEmailIgnoreCase(email.trim());
    }

    @Transactional
    public void save(Member member) {
        member.setName(member.getName().trim());
        member.setEmail(member.getEmail().trim());
        memberRepository.save(member);
    }
}

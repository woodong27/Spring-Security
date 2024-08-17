package com.example.jwt.service;

import com.example.jwt.dto.member.JoinDto;
import com.example.jwt.entity.Member;
import com.example.jwt.error.exception.DuplicateNameException;
import com.example.jwt.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void join(JoinDto joinDto) {
        String name = joinDto.getName();
        if (memberRepository.existsByName(name)) {
            throw new DuplicateNameException("Name " + name + " is duplicated");
        }

        memberRepository.save(Member.builder()
                .name(joinDto.getName())
                .password(passwordEncoder.encode(joinDto.getPassword()))
                .role("ROLE_USER")
                .build());
    }
}

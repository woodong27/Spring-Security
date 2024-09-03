package com.example.jwt.service;

import com.example.jwt.dto.member.JoinDto;
import com.example.jwt.entity.Member;
import com.example.jwt.exception.custom.DuplicateNameException;
import com.example.jwt.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public JoinDto.Response join(JoinDto.Request request) {
        String name = request.getName();
        if (memberRepository.existsByName(name)) {
            throw new DuplicateNameException("Name " + name + " is duplicated");
        }

        Member member = memberRepository.save(Member.builder()
                .name(name)
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .build());

        return JoinDto.Response.builder()
                .id(member.getId())
                .name(member.getName())
                .build();
    }
}

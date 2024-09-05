package com.example.jwt.service;

import com.example.jwt.dto.member.JoinDto;
import com.example.jwt.entity.Member;
import com.example.jwt.entity.RefreshToken;
import com.example.jwt.exception.custom.DuplicateNameException;
import com.example.jwt.exception.custom.ReissueException;
import com.example.jwt.jwt.JwtPayload;
import com.example.jwt.jwt.JwtUtil;
import com.example.jwt.repository.MemberRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import static com.example.jwt.jwt.JwtUtil.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisService redisService;

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

    public String reissue(HttpServletRequest request, HttpServletResponse response) {
        Cookie refresh = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(AUTH_HEADER))
                .findFirst()
                .orElseThrow(() -> new ReissueException("Refresh token not found"));

        String token = refresh.getValue();
        try {
            JwtPayload payload = jwtUtil.verify(token);
            Long id = payload.getId();
            if (!redisService.exist(id, token)) throw new Exception("Refresh token not found");
            if (!payload.getCategory().equals("refresh")) throw new Exception("Invalid refresh token");
            String accessToken = jwtUtil.generate("access", id, payload.getName(), payload.getRole(), ACCESS_EXPIRATION);
            String refreshToken = jwtUtil.generate("refresh", id, payload.getName(), payload.getRole(), REFRESH_EXPIRATION_MILLI);
            redisService.delete(id);
            redisService.save(id, refreshToken);

            response.setHeader(AUTH_HEADER, accessToken);
            response.addCookie(cookie(refreshToken));
            return "Access token reissued";
        } catch (ExpiredJwtException e) {
            throw new ReissueException("Refresh token expired");
        } catch (Exception e) {
            throw new ReissueException(e.getMessage());
        }
    }
}

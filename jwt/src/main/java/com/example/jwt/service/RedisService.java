package com.example.jwt.service;

import com.example.jwt.entity.RefreshToken;
import com.example.jwt.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final RefreshTokenRepository repository;

    @Transactional
    public void save(Long userId, String token) {
        repository.save(RefreshToken.builder()
                .id(userId)
                .token(token)
                .build());
    }

    public boolean exist(Long id, String token) {
        return repository.findById(id)
                .map(refresh -> refresh.getToken().equals(token))
                .orElse(false);
    }

    @Transactional
    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (Exception e) {
            log.warn("Failed to delete refresh token(does not exist)");
        }
    }
}

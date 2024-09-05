package com.example.jwt.repository;

import com.example.jwt.entity.RefreshToken;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;

@EnableRedisRepositories
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {

    boolean existsByToken(String token);
}

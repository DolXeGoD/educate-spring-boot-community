package com.gbsw.board.service;

import com.gbsw.board.entity.RedisRefreshToken;
import com.gbsw.board.repository.RedisRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RedisRefreshTokenService {

    private final RedisRefreshTokenRepository repository;

    public void save(String token, String username) {
        RedisRefreshToken refreshToken = RedisRefreshToken.builder()
                .token(token)
                .username(username)
                .expiryDate(LocalDateTime.now().plusDays(1))
                .build();
        repository.save(refreshToken);
    }

    public RedisRefreshToken findByToken(String token) {
        return repository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 리프레시 토큰입니다."));
    }

    public void delete(String token) {
        repository.deleteById(token);
    }
}

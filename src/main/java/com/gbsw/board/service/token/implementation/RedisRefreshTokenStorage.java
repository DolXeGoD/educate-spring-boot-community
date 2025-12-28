package com.gbsw.board.service.token.implementation;

import com.gbsw.board.dto.auth.RefreshTokenDto;
import com.gbsw.board.entity.RedisRefreshToken;
import com.gbsw.board.repository.RedisRefreshTokenRepository;
import com.gbsw.board.service.token.RefreshTokenStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "token.storage", havingValue = "redis")
public class RedisRefreshTokenStorage implements RefreshTokenStorage {

    private final RedisRefreshTokenRepository redisRefreshTokenRepository;

    @Override
    public void save(String token, String username, LocalDateTime expiryDate) {
        redisRefreshTokenRepository.save(RedisRefreshToken.builder()
                .token(token)
                .username(username)
                .expiryDate(expiryDate)
                .build());
    }

    @Override
    public Optional<RefreshTokenDto> findByToken(String token) {
        return redisRefreshTokenRepository.findByToken(token)
                .map(entity -> RefreshTokenDto.builder()
                        .token(entity.getToken())
                        .username(entity.getUsername())
                        .expiryDate(entity.getExpiryDate())
                        .build());
    }

    @Override
    public void deleteByToken(String token) {
        redisRefreshTokenRepository.deleteById(token);
    }

    @Override
    public void deleteByUsername(String username) {
        redisRefreshTokenRepository.deleteByUsername(username);
    }
}

package com.gbsw.board.service.token.implementation;

import com.gbsw.board.dto.auth.RefreshTokenDto;
import com.gbsw.board.entity.RefreshToken;
import com.gbsw.board.repository.RefreshTokenRepository;
import com.gbsw.board.service.token.RefreshTokenStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "token.storage", havingValue = "mysql")
public class JpaRefreshTokenStorage implements RefreshTokenStorage {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void save(String token, String username, LocalDateTime expiryDate) {
        refreshTokenRepository.save(RefreshToken.builder()
                .token(token)
                .username(username)
                .expiryDate(expiryDate)
                .build());
    }

    @Override
    public Optional<RefreshTokenDto> findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .map(entity -> RefreshTokenDto.builder()
                        .token(entity.getToken())
                        .username(entity.getUsername())
                        .expiryDate(entity.getExpiryDate())
                        .build());
    }

    @Override
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteById(token);
    }

    @Override
    public void deleteByUsername(String username) {
        refreshTokenRepository.deleteByUsername(username);
    }
}

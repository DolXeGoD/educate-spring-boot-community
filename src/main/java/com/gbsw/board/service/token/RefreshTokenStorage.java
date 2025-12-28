package com.gbsw.board.service.token;

import com.gbsw.board.dto.auth.RefreshTokenDto;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenStorage {
    void save(String token, String username, LocalDateTime expiryDate);

    Optional<RefreshTokenDto> findByToken(String token);

    void deleteByToken(String token);

    void deleteByUsername(String username);
}

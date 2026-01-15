package com.gbsw.board.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenDto {
    private String token;
    private String username;
    private LocalDateTime expiryDate;
}

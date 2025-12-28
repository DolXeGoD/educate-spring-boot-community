package com.gbsw.board.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefreshRequest {
    @NotBlank(message = "리프레시 토큰은 필수항목입니다.")
    private String refreshToken;
}
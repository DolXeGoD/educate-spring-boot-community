package com.gbsw.board.controller;

import com.gbsw.board.dto.auth.LoginRequest;
import com.gbsw.board.dto.auth.RefreshRequest;
import com.gbsw.board.dto.auth.SignupRequest;
import com.gbsw.board.dto.auth.TokenResponse;
import com.gbsw.board.dto.global.ApiResponse;
import com.gbsw.board.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Object>> signup(@Valid @RequestBody SignupRequest request) {
        // 1. 임시 회원가입 및 코드 생성, 이메일 발송
        authService.createPendingUser(request);

        return ResponseEntity.ok(ApiResponse.ok("회원가입이 완료되었습니다. 이메일을 확인하여 인증을 진행해주세요."));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<ApiResponse<Object>> verifyCode(
            @Valid @RequestBody com.gbsw.board.dto.auth.VerifyCodeRequest request) {
        authService.verifyCode(request);
        return ResponseEntity.ok(ApiResponse.ok("이메일 인증이 완료되었습니다."));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse tokenResponse = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok(tokenResponse));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.refresh(request)));
    }

}
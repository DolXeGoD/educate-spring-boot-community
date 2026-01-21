package com.gbsw.board.controller;

import com.gbsw.board.dto.auth.TokenResponse;
import com.gbsw.board.dto.global.ApiResponse;
import com.gbsw.board.dto.passwordless.LoginTriggerResponse;
import com.gbsw.board.dto.passwordless.RegisterResponse;
import com.gbsw.board.dto.passwordless.StatusResponse;
import com.gbsw.board.service.PasswordlessService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/passwordless")
@RequiredArgsConstructor
public class PasswordlessController {

    private final PasswordlessService passwordlessService;

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<StatusResponse>> checkStatus(@RequestParam String userId) {
        StatusResponse response = passwordlessService.checkStatus(userId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@RequestParam String userId) {
        RegisterResponse response = passwordlessService.register(userId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/login-trigger")
    public ResponseEntity<ApiResponse<LoginTriggerResponse>> triggerLogin(
            @RequestParam String userId,
            HttpServletRequest request) {
        LoginTriggerResponse response = passwordlessService.triggerLogin(userId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/result")
    public ResponseEntity<ApiResponse<TokenResponse>> checkResult(
            @RequestParam String userId,
            @RequestParam String sessionId) {
        TokenResponse response = passwordlessService.checkResult(userId, sessionId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/cancel")
    public ResponseEntity<ApiResponse<Void>> cancel(
            @RequestParam String userId,
            @RequestParam String sessionId) {
        passwordlessService.cancel(userId, sessionId);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @PostMapping("/withdrawal")
    public ResponseEntity<ApiResponse<Void>> withdraw() {
        passwordlessService.withdraw();
        return ResponseEntity.ok(ApiResponse.ok());
    }
}

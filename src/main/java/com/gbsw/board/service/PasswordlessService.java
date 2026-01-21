package com.gbsw.board.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gbsw.board.client.PasswordlessApiClient;
import com.gbsw.board.dto.auth.TokenResponse;
import com.gbsw.board.dto.passwordless.*;
import com.gbsw.board.entity.User;
import com.gbsw.board.enums.UserStatus;
import com.gbsw.board.exceptions.AuthenticationFailureException;
import com.gbsw.board.repository.UserRepository;
import com.gbsw.board.security.JwtTokenProvider;
import com.gbsw.board.service.token.RefreshTokenStorage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordlessService {

    private final PasswordlessApiClient apiClient;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenStorage refreshTokenStorage;

    // 패스워드리스 사용 여부 확인
    @Transactional(readOnly = true)
    public StatusResponse checkStatus(String userId) {
        validateUser(userId);

        ServingApiResponse<StatusResponse> response = apiClient.checkStatus(userId);
        return response.getData();
    }

    // 패스워드리스 신규 등록
    @Transactional
    public RegisterResponse register(String userId) {
        validateUser(userId);

        ServingApiResponse<RegisterResponse> response = apiClient.register(userId);
        return response.getData();
    }

    // 로그인 요청
    @Transactional
    public LoginTriggerResponse triggerLogin(String userId, HttpServletRequest request) {
        validateUser(userId);

        String clientIp = extractClientIp(request);
        ServingApiResponse<LoginTriggerResponse> response = apiClient.triggerLogin(userId, clientIp);
        return response.getData();
    }

    // 로그인 결과 확인
    @Transactional
    public TokenResponse checkResult(String userId, String sessionId) {
        validateUser(userId);

        ServingApiResponse<ResultResponse> response = apiClient.checkResult(userId, sessionId);
        if(!Objects.equals(response.getData().getAuth(), "Y")){
            throw new AuthenticationFailureException("인증이 완료되지 않았습니다.");
        }

        String accessToken = jwtTokenProvider.createToken(userId);
        String refreshToken = jwtTokenProvider.createRefreshToken(userId);

        refreshTokenStorage.deleteByUsername(userId);
        refreshTokenStorage.save(refreshToken, userId, LocalDateTime.now().plusDays(7));

        return new TokenResponse(accessToken, refreshToken);
    }

    // 인증 취소
    @Transactional(readOnly = true)
    public void cancel(String userId, String sessionId) {
        validateUser(userId);

        apiClient.cancel(userId, sessionId);
    }

    // 패스워드리스 인증 해지
    // (이미 로그인한 유저만 호출 가능)
    @Transactional
    public void withdraw() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        apiClient.withdraw(userId);
    }

    private void validateUser(String userId) {
        User user = userRepository.findByUsername(userId)
                .orElseThrow(() -> new AuthenticationFailureException("사용자를 찾을 수 없습니다."));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AuthenticationFailureException("비활성화된 사용자입니다.");
        }
    }

    private String extractClientIp(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }
}

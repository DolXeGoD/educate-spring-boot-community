package com.gbsw.board.service;

import com.gbsw.board.dto.auth.LoginRequest;
import com.gbsw.board.dto.auth.RefreshRequest;
import com.gbsw.board.dto.auth.SignupRequest;
import com.gbsw.board.dto.auth.TokenResponse;
import com.gbsw.board.dto.auth.VerifyCodeRequest;
import com.gbsw.board.entity.EmailVerification;
import com.gbsw.board.entity.RedisRefreshToken;
import com.gbsw.board.entity.RefreshToken;
import com.gbsw.board.entity.User;
import com.gbsw.board.enums.AuthLevel;
import com.gbsw.board.enums.UserStatus;
import com.gbsw.board.repository.EmailVerificationRepository;
import com.gbsw.board.repository.RefreshTokenRepository;
import com.gbsw.board.repository.UserRepository;
import com.gbsw.board.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RedisRefreshTokenService redisRefreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final EmailVerificationRepository emailVerificationRepository;

    // 인증번호 발송 (임시 회원가입)
    @Transactional
    public void createPendingUser(SignupRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다.");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .status(UserStatus.PENDING)
                .authLevel(AuthLevel.USER)
                .build();

        userRepository.save(user);

        // 인증 코드 생성
        String code = generateVerificationCode();

        // 인증 정보 저장
        EmailVerification verification = EmailVerification.builder()
                .user(user)
                .code(code)
                .expiresAt(LocalDateTime.now().plusMinutes(5)) // 5분 유효
                .isVerified(false)
                .build();

        emailVerificationRepository.save(verification);

        // 이메일 전송
        emailService.sendVerificationEmail(user.getEmail(), code);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    // 인증번호 검증
    @Transactional
    public void verifyCode(VerifyCodeRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        EmailVerification verification = emailVerificationRepository.findByUserAndCode(user, request.getCode())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 인증번호입니다."));

        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("인증번호가 만료되었습니다.");
        }

        if (verification.isVerified()) {
            throw new IllegalArgumentException("이미 인증된 번호입니다.");
        }

        verification.setVerified(true);
        user.setStatus(UserStatus.ACTIVE);
    }

    // 로그인 및 토큰 발급
    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException("이메일 인증이 완료되지 않았습니다.");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        String accessToken = jwtTokenProvider.createToken(authentication.getName());
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication.getName());

        // redisRefreshTokenService.save(refreshToken, request.getUsername()); // Redis
        // 저장

        /* 아래 코드는, MySQL을 통한 관리를 진행할 때 사용 */
        refreshTokenRepository.deleteByUsername(request.getUsername());
        refreshTokenRepository.save(RefreshToken.builder()
                .token(refreshToken)
                .username(request.getUsername())
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build());

        return new TokenResponse(accessToken, refreshToken);
    }

    // 리프레시 토큰을 이용한 엑세스 토큰 재발급
    public TokenResponse refresh(RefreshRequest request) {
        /* 아래 코드는, Redis를 통한 관리를 진행할 때 사용 */
        // RedisRefreshToken token =
        // redisRefreshTokenService.findByToken(request.getRefreshToken());
        //
        // if (token == null) {
        // throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
        // }
        //
        // if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
        // redisRefreshTokenService.delete(request.getRefreshToken());
        // throw new RuntimeException("리프레시 토큰이 만료되었습니다.");
        // }

        /* 아래 코드는, MySQL을 통한 관리를 진행할 때 사용 */
        RefreshToken token = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("유효하지 않은 리프레시 토큰입니다."));

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("리프레시 토큰이 만료되었습니다.");
        }

        // AccessToken 만 새로 발급, RefreshToken 은 그대로 유지
        String newAccessToken = jwtTokenProvider.createToken(token.getUsername());

        return new TokenResponse(newAccessToken, token.getToken());
    }
}
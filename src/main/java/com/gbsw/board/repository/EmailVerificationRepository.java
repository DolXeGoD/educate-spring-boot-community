package com.gbsw.board.repository;

import com.gbsw.board.entity.EmailVerification;
import com.gbsw.board.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByUserAndCode(User user, String code);

    Optional<EmailVerification> findFirstByUserOrderByExpiresAtDesc(User user);
}

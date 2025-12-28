package com.gbsw.board.repository;

import com.gbsw.board.entity.AccessTokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AccessTokenBlacklistRepository extends JpaRepository<AccessTokenBlacklist, Integer> {
    Optional<AccessTokenBlacklist> findByToken(String token);

    void deleteAllByExpirationDateTimeBefore(LocalDateTime dateTime);
}

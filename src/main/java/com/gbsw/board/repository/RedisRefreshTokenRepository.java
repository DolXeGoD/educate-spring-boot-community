package com.gbsw.board.repository;

import com.gbsw.board.entity.RedisRefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RedisRefreshTokenRepository extends CrudRepository<RedisRefreshToken, String> {
    Optional<RedisRefreshToken> findByToken(String token);
    void deleteByUsername(String username); // 따로 구현 필요
    Optional<RedisRefreshToken> findByUsername(String username); // 따로 구현 필요
}

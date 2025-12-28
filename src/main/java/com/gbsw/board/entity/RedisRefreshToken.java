package com.gbsw.board.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash(value = "refresh_token", timeToLive = 86400) // 1일 = 60*60*24
public class RedisRefreshToken {

    @Id
    private String token;

    private String username;

    private LocalDateTime expiryDate;
}

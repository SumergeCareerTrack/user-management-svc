package com.sumerge.careertrack.user_management_svc.entities;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@RedisHash(value = "TOKENS", timeToLive = 3600)
@Data
@Builder
@AllArgsConstructor
public class UserToken {
    @Id
    String email;

    UUID userId;

    String token;
}

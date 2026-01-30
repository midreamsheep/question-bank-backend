package indi.midreamsheep.vegetable.backend.infrastructure.security;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * JWT 配置项。
 *
 * @param issuer 签发方
 * @param secret 签名密钥
 * @param expireSeconds 过期时间（秒）
 */
@Validated
@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(
        @NotBlank String issuer,
        @NotBlank String secret,
        @Min(60) long expireSeconds
) {
}

package indi.midreamsheep.vegetable.backend.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import indi.midreamsheep.vegetable.backend.features.auth.domain.port.TokenPort;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/**
 * JWT token 端口实现：基于 HMAC256 签名。
 */
@Component
public class JwtTokenAdapter implements TokenPort {

    private final JwtProperties properties;
    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    /**
     * 构造 JWT token 适配器。
     *
     * @param properties JWT 配置项
     */
    public JwtTokenAdapter(JwtProperties properties) {
        this.properties = properties;
        this.algorithm = Algorithm.HMAC256(properties.secret());
        this.verifier = JWT.require(this.algorithm)
                .withIssuer(properties.issuer())
                .build();
    }

    /**
     * 生成 JWT token。
     *
     * @param subject token 主体
     * @return token 字符串
     */
    @Override
    public String generate(String subject) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(properties.expireSeconds());
        return JWT.create()
                .withIssuer(properties.issuer())
                .withSubject(subject)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiresAt))
                .sign(algorithm);
    }

    /**
     * 校验 JWT token 并提取主体。
     *
     * @param token token 字符串
     * @return 主体（校验失败返回空）
     */
    @Override
    public Optional<String> verifyAndGetSubject(String token) {
        try {
            DecodedJWT decoded = verifier.verify(token);
            return Optional.ofNullable(decoded.getSubject());
        } catch (JWTVerificationException ex) {
            return Optional.empty();
        }
    }
}

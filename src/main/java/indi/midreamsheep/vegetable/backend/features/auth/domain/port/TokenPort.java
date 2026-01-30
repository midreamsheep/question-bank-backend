package indi.midreamsheep.vegetable.backend.features.auth.domain.port;

import java.util.Optional;

/**
 * Token 生成与校验端口：用于 JWT 等 token 的签发与验证。
 */
public interface TokenPort {

    /**
     * 生成 token。
     *
     * @param subject token 主体（通常为用户标识）
     * @return token 字符串
     */
    String generate(String subject);

    /**
     * 校验 token 并提取主体。
     *
     * @param token token 字符串
     * @return 主体（校验失败返回空）
     */
    Optional<String> verifyAndGetSubject(String token);
}

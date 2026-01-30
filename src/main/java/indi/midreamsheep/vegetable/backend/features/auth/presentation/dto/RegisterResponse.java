package indi.midreamsheep.vegetable.backend.features.auth.presentation.dto;

/**
 * 注册响应 DTO。
 *
 * @param userId 用户ID
 * @param token JWT token
 */
public record RegisterResponse(
        long userId,
        String token
) {
}

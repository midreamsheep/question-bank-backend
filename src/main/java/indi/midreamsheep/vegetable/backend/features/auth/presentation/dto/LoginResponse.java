package indi.midreamsheep.vegetable.backend.features.auth.presentation.dto;

/**
 * 登录响应 DTO。
 *
 * @param token JWT token
 */
public record LoginResponse(
        String token
) {
}

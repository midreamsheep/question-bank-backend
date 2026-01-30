package indi.midreamsheep.vegetable.backend.features.file.presentation.dto;

/**
 * 预签名 URL 响应 DTO。
 *
 * @param url 预签名 URL
 */
public record PresignedUrlResponse(
        String url
) {
}


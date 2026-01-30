package indi.midreamsheep.vegetable.backend.features.file.presentation.dto;

/**
 * 文件分享信息响应 DTO。
 *
 * @param shareKey 分享 key
 * @param shareUrl 永久链接（相对路径）
 */
public record FileShareResponse(
        String shareKey,
        String shareUrl
) {
}


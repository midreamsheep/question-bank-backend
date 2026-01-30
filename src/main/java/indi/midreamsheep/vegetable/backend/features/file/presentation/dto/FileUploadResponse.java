package indi.midreamsheep.vegetable.backend.features.file.presentation.dto;

/**
 * 文件上传响应 DTO。
 *
 * @param id 文件ID（vf_file_object.id）
 * @param shareKey 分享 key（用于永久链接）
 * @param shareUrl 永久链接（相对路径）
 * @param objectKey 对象存储 key
 * @param originalFilename 原始文件名
 * @param size 文件大小（字节）
 * @param contentType 文件类型（MIME）
 */
public record FileUploadResponse(
        long id,
        String shareKey,
        String shareUrl,
        String objectKey,
        String originalFilename,
        long size,
        String contentType
) {
}

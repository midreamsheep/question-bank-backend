package indi.midreamsheep.vegetable.backend.features.file.domain.command;

/**
 * 文件对象创建命令。
 *
 * @param shareKey 分享 key（永久链接凭证）
 * @param objectKey 对象存储 key
 * @param originalFilename 原始文件名
 * @param size 文件大小（字节）
 * @param contentType 文件类型（MIME）
 * @param uploaderId 上传者用户ID（可为空）
 */
public record FileObjectCreateCommand(
        String shareKey,
        String objectKey,
        String originalFilename,
        long size,
        String contentType,
        Long uploaderId
) {
}

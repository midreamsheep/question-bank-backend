package indi.midreamsheep.vegetable.backend.features.file.domain.model;

import java.time.LocalDateTime;

/**
 * 文件对象数据（对应 vf_file_object）。
 *
 * @param id 文件ID
 * @param shareKey 分享 key（永久链接凭证）
 * @param objectKey 对象存储 key
 * @param originalFilename 原始文件名
 * @param size 文件大小（字节）
 * @param contentType 文件类型（MIME）
 * @param uploaderId 上传者用户ID（可为空）
 * @param createdAt 创建时间
 */
public record FileObjectData(
        long id,
        String shareKey,
        String objectKey,
        String originalFilename,
        long size,
        String contentType,
        Long uploaderId,
        LocalDateTime createdAt
) {
}

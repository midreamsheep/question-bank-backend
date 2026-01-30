package indi.midreamsheep.vegetable.backend.features.file.domain;

/**
 * 已存储文件的领域模型。
 *
 * @param objectKey 对象存储中的唯一标识
 * @param size 文件大小（字节）
 * @param contentType 文件类型（MIME）
 */
public record StoredFile(
        String objectKey,
        long size,
        String contentType
) {
}


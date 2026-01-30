package indi.midreamsheep.vegetable.backend.features.file.domain.port;

import indi.midreamsheep.vegetable.backend.features.file.domain.StoredFile;

import java.io.InputStream;

/**
 * 文件存储端口：定义领域对“文件存储能力”的依赖。
 */
public interface FileStoragePort {

    /**
     * 上传文件到对象存储。
     *
     * @param objectKey 对象 key（由调用方生成并确保唯一性）
     * @param inputStream 文件流
     * @param size 文件大小（字节）
     * @param contentType 文件类型（MIME）
     * @return 已存储文件信息
     */
    StoredFile upload(String objectKey, InputStream inputStream, long size, String contentType);

    /**
     * 打开对象输入流（用于后端代理下载/图片展示）。
     *
     * @param objectKey 对象 key
     * @return 输入流（调用方负责关闭）
     */
    InputStream openStream(String objectKey);

    /**
     * 生成下载用的预签名 URL。
     *
     * @param objectKey 对象 key
     * @param expiresSeconds 有效期（秒）
     * @return 预签名 URL
     */
    String presignedGetUrl(String objectKey, int expiresSeconds);
}

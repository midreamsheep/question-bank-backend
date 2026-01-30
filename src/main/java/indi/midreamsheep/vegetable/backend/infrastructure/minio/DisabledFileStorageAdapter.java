package indi.midreamsheep.vegetable.backend.infrastructure.minio;

import indi.midreamsheep.vegetable.backend.common.error.BizException;
import indi.midreamsheep.vegetable.backend.common.error.ErrorCode;
import indi.midreamsheep.vegetable.backend.features.file.domain.StoredFile;
import indi.midreamsheep.vegetable.backend.features.file.domain.port.FileStoragePort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * MinIO 未启用时的占位实现：用于提供明确错误提示。
 */
@Component
@ConditionalOnProperty(prefix = "minio", name = "enabled", havingValue = "false", matchIfMissing = true)
public class DisabledFileStorageAdapter implements FileStoragePort {

    /**
     * 上传文件（MinIO 未启用时直接报错）。
     *
     * @param objectKey 对象 key
     * @param inputStream 文件流
     * @param size 文件大小（字节）
     * @param contentType 文件类型（MIME）
     * @return 不返回
     */
    @Override
    public StoredFile upload(String objectKey, InputStream inputStream, long size, String contentType) {
        throw new BizException(ErrorCode.SERVICE_UNAVAILABLE, "MinIO 未启用，请配置 minio.enabled=true");
    }

    /**
     * 打开对象输入流（MinIO 未启用时直接报错）。
     *
     * @param objectKey 对象 key
     * @return 不返回
     */
    @Override
    public InputStream openStream(String objectKey) {
        throw new BizException(ErrorCode.SERVICE_UNAVAILABLE, "MinIO 未启用，请配置 minio.enabled=true");
    }

    /**
     * 生成预签名 URL（MinIO 未启用时直接报错）。
     *
     * @param objectKey 对象 key
     * @param expiresSeconds 有效期（秒）
     * @return 不返回
     */
    @Override
    public String presignedGetUrl(String objectKey, int expiresSeconds) {
        throw new BizException(ErrorCode.SERVICE_UNAVAILABLE, "MinIO 未启用，请配置 minio.enabled=true");
    }
}

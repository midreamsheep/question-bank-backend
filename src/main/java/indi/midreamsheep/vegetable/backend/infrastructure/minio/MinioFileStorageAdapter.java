package indi.midreamsheep.vegetable.backend.infrastructure.minio;

import indi.midreamsheep.vegetable.backend.common.error.BizException;
import indi.midreamsheep.vegetable.backend.common.error.ErrorCode;
import indi.midreamsheep.vegetable.backend.features.file.domain.StoredFile;
import indi.midreamsheep.vegetable.backend.features.file.domain.port.FileStoragePort;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * MinIO 文件存储适配器：实现 {@link FileStoragePort}。
 */
@Component
@ConditionalOnProperty(prefix = "minio", name = "enabled", havingValue = "true")
public class MinioFileStorageAdapter implements FileStoragePort {

    private final MinioClient minioClient;
    private final MinioProperties properties;

    /**
     * 构造 MinIO 文件存储适配器。
     *
     * @param minioClient MinIO 客户端
     * @param properties MinIO 配置项
     */
    public MinioFileStorageAdapter(MinioClient minioClient, MinioProperties properties) {
        this.minioClient = minioClient;
        this.properties = properties;
    }

    /**
     * 上传文件到 MinIO。
     *
     * @param objectKey 对象 key
     * @param inputStream 文件流
     * @param size 文件大小（字节）
     * @param contentType 文件类型（MIME）
     * @return 已存储文件
     */
    @Override
    public StoredFile upload(String objectKey, InputStream inputStream, long size, String contentType) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(properties.bucket())
                    .object(objectKey)
                    .contentType(contentType)
                    .stream(inputStream, size, -1)
                    .build());
            return new StoredFile(objectKey, size, contentType);
        } catch (Exception ex) {
            throw new BizException(ErrorCode.SERVICE_UNAVAILABLE, "文件存储服务不可用");
        }
    }

    /**
     * 打开对象输入流（用于后端代理下载/图片展示）。
     *
     * @param objectKey 对象 key
     * @return 输入流（调用方负责关闭）
     */
    @Override
    public InputStream openStream(String objectKey) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(properties.bucket())
                    .object(objectKey)
                    .build());
        } catch (Exception ex) {
            throw new BizException(ErrorCode.SERVICE_UNAVAILABLE, "文件存储服务不可用");
        }
    }

    /**
     * 生成下载用的预签名 URL。
     *
     * @param objectKey 对象 key
     * @param expiresSeconds 有效期（秒）
     * @return 预签名 URL
     */
    @Override
    public String presignedGetUrl(String objectKey, int expiresSeconds) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(properties.bucket())
                    .object(objectKey)
                    .expiry(expiresSeconds)
                    .build());
        } catch (Exception ex) {
            throw new BizException(ErrorCode.SERVICE_UNAVAILABLE, "文件存储服务不可用");
        }
    }
}

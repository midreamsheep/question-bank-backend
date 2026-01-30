package indi.midreamsheep.vegetable.backend.infrastructure.minio;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

/**
 * MinIO 配置项。
 *
 * <p>注意：当 {@code enabled=true} 时，必须提供 endpoint/accessKey/secretKey/bucket。</p>
 *
 * @param enabled 是否启用 MinIO
 * @param endpoint MinIO 服务地址（例如：http://localhost:9000）
 * @param accessKey 访问 Key
 * @param secretKey 访问 Secret
 * @param bucket 存储桶名称
 * @param autoCreateBucket 是否自动创建 bucket
 * @param presignExpireSeconds 预签名 URL 默认有效期（秒）
 */
@ConfigurationProperties(prefix = "minio")
public record MinioProperties(
        boolean enabled,
        String endpoint,
        String accessKey,
        String secretKey,
        String bucket,
        boolean autoCreateBucket,
        long presignExpireSeconds
) {

    /**
     * 当启用 MinIO 时，校验配置是否齐全。
     */
    public void validateWhenEnabled() {
        if (!enabled) {
            return;
        }
        if (!StringUtils.hasText(endpoint)) {
            throw new IllegalStateException("MinIO 已启用，但 minio.endpoint 为空");
        }
        if (!StringUtils.hasText(accessKey)) {
            throw new IllegalStateException("MinIO 已启用，但 minio.access-key 为空");
        }
        if (!StringUtils.hasText(secretKey)) {
            throw new IllegalStateException("MinIO 已启用，但 minio.secret-key 为空");
        }
        if (!StringUtils.hasText(bucket)) {
            throw new IllegalStateException("MinIO 已启用，但 minio.bucket 为空");
        }
    }
}


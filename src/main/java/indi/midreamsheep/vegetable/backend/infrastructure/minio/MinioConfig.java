package indi.midreamsheep.vegetable.backend.infrastructure.minio;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 客户端与初始化配置。
 */
@Configuration
@EnableConfigurationProperties(MinioProperties.class)
public class MinioConfig {

    private static final Logger log = LoggerFactory.getLogger(MinioConfig.class);

    /**
     * 创建 MinIO 客户端（仅在启用 MinIO 时生效）。
     *
     * @param properties MinIO 配置项
     * @return MinIO 客户端
     */
    @Bean
    @ConditionalOnProperty(prefix = "minio", name = "enabled", havingValue = "true")
    public MinioClient minioClient(MinioProperties properties) {
        properties.validateWhenEnabled();
        return MinioClient.builder()
                .endpoint(properties.endpoint())
                .credentials(properties.accessKey(), properties.secretKey())
                .build();
    }

    /**
     * MinIO 初始化：按需自动创建 bucket（仅在启用 MinIO 且 auto-create-bucket=true 时生效）。
     *
     * @param properties MinIO 配置项
     * @param minioClient MinIO 客户端
     * @return 应用启动任务
     */
    @Bean
    @ConditionalOnProperty(prefix = "minio", name = {"enabled", "auto-create-bucket"}, havingValue = "true")
    public ApplicationRunner minioBucketInitializer(MinioProperties properties, MinioClient minioClient) {
        return args -> {
            try {
                boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                        .bucket(properties.bucket())
                        .build());
                if (!exists) {
                    minioClient.makeBucket(MakeBucketArgs.builder()
                            .bucket(properties.bucket())
                            .build());
                    log.info("event=minio_bucket_created bucket={}", properties.bucket());
                }
            } catch (Exception ex) {
                throw new IllegalStateException("MinIO bucket 初始化失败", ex);
            }
        };
    }
}


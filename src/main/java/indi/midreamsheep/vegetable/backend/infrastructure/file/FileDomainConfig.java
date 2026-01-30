package indi.midreamsheep.vegetable.backend.infrastructure.file;

import indi.midreamsheep.vegetable.backend.features.file.domain.FileDomainService;
import indi.midreamsheep.vegetable.backend.features.file.domain.port.FileObjectRepositoryPort;
import indi.midreamsheep.vegetable.backend.features.file.domain.port.FileStoragePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 文件领域服务装配。
 */
@Configuration
public class FileDomainConfig {

    /**
     * 构造文件领域服务。
     *
     * @param fileStoragePort 文件存储端口
     * @param fileObjectRepositoryPort 文件对象仓储端口
     * @return 文件领域服务
     */
    @Bean
    public FileDomainService fileDomainService(
            FileStoragePort fileStoragePort,
            FileObjectRepositoryPort fileObjectRepositoryPort
    ) {
        return new FileDomainService(fileStoragePort, fileObjectRepositoryPort);
    }
}

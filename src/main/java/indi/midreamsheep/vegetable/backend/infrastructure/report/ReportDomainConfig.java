package indi.midreamsheep.vegetable.backend.infrastructure.report;

import indi.midreamsheep.vegetable.backend.features.report.domain.ReportDomainService;
import indi.midreamsheep.vegetable.backend.features.report.domain.port.ReportRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 举报领域服务装配。
 */
@Configuration
public class ReportDomainConfig {

    /**
     * 构造举报领域服务。
     *
     * @param reportRepositoryPort 举报仓储端口
     * @return 举报领域服务
     */
    @Bean
    public ReportDomainService reportDomainService(ReportRepositoryPort reportRepositoryPort) {
        return new ReportDomainService(reportRepositoryPort);
    }
}


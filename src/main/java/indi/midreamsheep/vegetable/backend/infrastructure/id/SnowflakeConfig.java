package indi.midreamsheep.vegetable.backend.infrastructure.id;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 雪花 ID 配置：提供全局 Snowflake 生成器。
 */
@Configuration
@EnableConfigurationProperties(SnowflakeProperties.class)
public class SnowflakeConfig {

    /**
     * 构造 Hutool Snowflake 生成器。
     *
     * @param properties 雪花配置
     * @return Snowflake 实例
     */
    @Bean
    public Snowflake snowflake(SnowflakeProperties properties) {
        properties.validate();
        return IdUtil.getSnowflake(properties.workerId(), properties.datacenterId());
    }
}

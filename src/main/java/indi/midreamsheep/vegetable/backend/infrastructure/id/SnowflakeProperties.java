package indi.midreamsheep.vegetable.backend.infrastructure.id;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 雪花 ID 配置项。
 *
 * @param workerId 工作节点 ID（0-31）
 * @param datacenterId 数据中心 ID（0-31）
 */
@ConfigurationProperties(prefix = "id.snowflake")
public record SnowflakeProperties(
        long workerId,
        long datacenterId
) {

    /**
     * 校验雪花 ID 配置范围。
     */
    public void validate() {
        if (workerId < 0 || workerId > 31) {
            throw new IllegalStateException("id.snowflake.worker-id 必须在 0-31 范围内");
        }
        if (datacenterId < 0 || datacenterId > 31) {
            throw new IllegalStateException("id.snowflake.datacenter-id 必须在 0-31 范围内");
        }
    }
}

package indi.midreamsheep.vegetable.backend.infrastructure.id;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.springframework.stereotype.Component;

/**
 * MyBatis-Plus 自定义雪花 ID 生成器（使用 Hutool）。
 */
@Component
public class SnowflakeIdentifierGenerator implements IdentifierGenerator {

    private final Snowflake snowflake;

    /**
     * 构造雪花 ID 生成器。
     *
     * @param snowflake Hutool Snowflake
     */
    public SnowflakeIdentifierGenerator(Snowflake snowflake) {
        this.snowflake = snowflake;
    }

    @Override
    public Long nextId(Object entity) {
        return snowflake.nextId();
    }

    @Override
    public String nextUUID(Object entity) {
        return IdUtil.fastSimpleUUID();
    }
}

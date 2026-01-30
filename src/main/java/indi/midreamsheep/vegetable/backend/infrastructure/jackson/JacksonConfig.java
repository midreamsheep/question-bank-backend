package indi.midreamsheep.vegetable.backend.infrastructure.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Jackson 序列化配置：提供默认 {@link ObjectMapper}。
 */
@Configuration
public class JacksonConfig {

    /**
     * 创建并注册 Jackson 模块的 ObjectMapper。
     *
     * @return ObjectMapper
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }
}

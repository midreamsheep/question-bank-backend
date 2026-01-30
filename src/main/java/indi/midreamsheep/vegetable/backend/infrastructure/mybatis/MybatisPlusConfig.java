package indi.midreamsheep.vegetable.backend.infrastructure.mybatis;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置：扫描 {@link Mapper} 注解的 Mapper 接口。
 */
@Configuration
@MapperScan(basePackages = "indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper",
        annotationClass = Mapper.class)
public class MybatisPlusConfig {

    /**
     * MyBatis-Plus 拦截器。
     *
     * @return MybatisPlusInterceptor
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        return new MybatisPlusInterceptor();
    }
}

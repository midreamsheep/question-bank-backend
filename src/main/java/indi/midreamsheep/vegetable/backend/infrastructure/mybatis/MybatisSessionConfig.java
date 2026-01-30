package indi.midreamsheep.vegetable.backend.infrastructure.mybatis;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * MyBatis 会话配置：显式提供 SqlSessionFactory/SqlSessionTemplate。
 */
@Configuration
public class MybatisSessionConfig {

    /**
     * 创建 SqlSessionFactory。
     *
     * @param dataSource 数据源
     * @return SqlSessionFactory
     * @throws Exception 初始化异常
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(
            DataSource dataSource,
            MybatisPlusInterceptor mybatisPlusInterceptor,
            IdentifierGenerator identifierGenerator
    ) throws Exception {
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTypeAliasesPackage("indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity");
        factory.setPlugins(mybatisPlusInterceptor);
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setIdentifierGenerator(identifierGenerator);
        factory.setGlobalConfig(globalConfig);
        return factory.getObject();
    }

    /**
     * 创建 SqlSessionTemplate。
     *
     * @param sqlSessionFactory SqlSessionFactory
     * @return SqlSessionTemplate
     */
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}

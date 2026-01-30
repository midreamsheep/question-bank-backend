package indi.midreamsheep.vegetable.backend.infrastructure.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI（Swagger）配置。
 */
@Configuration
public class OpenApiConfig {

    /**
     * JWT Bearer 安全方案名称（在 OpenAPI 中使用）。
     */
    public static final String SECURITY_SCHEME_NAME = "BearerAuth";

    /**
     * 构建 OpenAPI 元信息与安全方案。
     *
     * @return OpenAPI 配置对象
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Vegetable Forum API")
                        .version("v1"))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .schemaRequirement(SECURITY_SCHEME_NAME, new SecurityScheme()
                        .name(SECURITY_SCHEME_NAME)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));
    }
}

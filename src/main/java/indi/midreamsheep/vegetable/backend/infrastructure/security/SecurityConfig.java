package indi.midreamsheep.vegetable.backend.infrastructure.security;

import indi.midreamsheep.vegetable.backend.features.auth.domain.AuthDomainService;
import indi.midreamsheep.vegetable.backend.features.auth.domain.port.CredentialPort;
import indi.midreamsheep.vegetable.backend.features.auth.domain.port.TokenPort;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security 配置：启用 JWT 无状态认证，并放行健康检查、登录、Swagger 等公共接口。
 */
@Configuration
@EnableConfigurationProperties({JwtProperties.class, AdminProperties.class})
public class SecurityConfig {

    /**
     * 构造认证领域服务 Bean。
     *
     * @param credentialPort 凭证端口
     * @param tokenPort token 端口
     * @return 认证领域服务
     */
    @Bean
    public AuthDomainService authDomainService(CredentialPort credentialPort, TokenPort tokenPort) {
        return new AuthDomainService(credentialPort, tokenPort);
    }

    /**
     * 提供一个空实现的 UserDetailsService，避免 Spring Boot 自动创建默认用户。
     *
     * @return UserDetailsService
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            throw new UsernameNotFoundException("No user details service is configured");
        };
    }

    /**
     * 密码编码器。
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * CORS 配置（用于前端本地开发联调）。
     *
     * @return CORS 配置源
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // allowCredentials=true 时不能使用 allowedOrigins="*"
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:5173",
                "http://127.0.0.1:5173"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("X-Request-Id"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * 配置安全过滤链：关闭表单/Basic/Session，启用 JWT Filter。
     *
     * @param http HttpSecurity
     * @param jwtAuthenticationFilter JWT 认证过滤器
     * @param authenticationEntryPoint 401 处理器
     * @param accessDeniedHandler 403 处理器
     * @return SecurityFilterChain
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            RestAuthenticationEntryPoint authenticationEntryPoint,
            RestAccessDeniedHandler accessDeniedHandler
    ) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/api/v1/health",
                                "/api/v1/auth/login",
                                "/api/v1/auth/register",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/problems",
                                "/api/v1/problems/*",
                                "/api/v1/problems/*/comments",
                                "/api/v1/problems/share/*",
                                "/api/v1/files/share/*",
                                "/api/v1/categories",
                                "/api/v1/problem-types",
                                "/api/v1/tags",
                                "/api/v1/collections",
                                "/api/v1/collections/*",
                                "/api/v1/collections/share/*",
                                "/api/v1/daily-problem",
                                "/api/v1/daily-problem/today",
                                "/api/v1/daily-problems"
                        ).permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}

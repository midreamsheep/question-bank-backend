package indi.midreamsheep.vegetable.backend.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import indi.midreamsheep.vegetable.backend.common.api.ApiResponse;
import indi.midreamsheep.vegetable.backend.common.error.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 认证入口点：未认证访问时统一返回 401 JSON 响应体。
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    /**
     * 构造认证入口点。
     *
     * @param objectMapper JSON 序列化器
     */
    public RestAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 处理未认证访问（401）。
     *
     * @param request 请求
     * @param response 响应
     * @param authException 认证异常
     * @throws IOException IO 异常
     * @throws ServletException Servlet 异常
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(),
                ApiResponse.error(ErrorCode.UNAUTHORIZED.code(), "Unauthorized"));
    }
}

package indi.midreamsheep.vegetable.backend.features.auth.presentation;

import indi.midreamsheep.vegetable.backend.common.api.ApiResponse;
import indi.midreamsheep.vegetable.backend.features.auth.domain.AuthDomainService;
import indi.midreamsheep.vegetable.backend.features.auth.presentation.dto.LoginRequest;
import indi.midreamsheep.vegetable.backend.features.auth.presentation.dto.LoginResponse;
import indi.midreamsheep.vegetable.backend.features.auth.presentation.dto.RegisterRequest;
import indi.midreamsheep.vegetable.backend.features.auth.presentation.dto.RegisterResponse;
import indi.midreamsheep.vegetable.backend.features.user.domain.UserDomainService;
import indi.midreamsheep.vegetable.backend.features.user.domain.command.UserRegisterCommand;
import indi.midreamsheep.vegetable.backend.features.user.domain.model.UserProfileData;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证相关接口（登录等）。
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthDomainService authDomainService;
    private final UserDomainService userDomainService;

    /**
     * 构造认证控制器。
     *
     * @param authDomainService 认证领域服务
     */
    public AuthController(AuthDomainService authDomainService, UserDomainService userDomainService) {
        this.authDomainService = authDomainService;
        this.userDomainService = userDomainService;
    }

    /**
     * 登录并返回 JWT token。
     *
     * @param request 登录请求
     * @return 统一响应体（包含 token）
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String username = request.username() == null ? null : request.username().trim();
        String token = authDomainService.login(username, request.password());
        return ApiResponse.ok(new LoginResponse(token));
    }

    /**
     * 注册并返回 JWT token。
     *
     * @param request 注册请求
     * @return 统一响应体（包含 token）
     */
    @PostMapping("/register")
    public ApiResponse<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        String username = request.username() == null ? null : request.username().trim();
        UserProfileData profile = userDomainService.register(new UserRegisterCommand(
                username,
                request.password(),
                request.nickname()
        ));
        String token = authDomainService.login(username, request.password());
        return ApiResponse.ok(new RegisterResponse(profile.id(), token));
    }

    /**
     * 退出登录（前端丢弃 token）。
     *
     * @return 统一响应体
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        return ApiResponse.ok();
    }
}

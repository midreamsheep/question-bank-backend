package indi.midreamsheep.vegetable.backend.features.user.presentation;

import indi.midreamsheep.vegetable.backend.common.api.ApiResponse;
import indi.midreamsheep.vegetable.backend.common.error.BizException;
import indi.midreamsheep.vegetable.backend.common.error.ErrorCode;
import indi.midreamsheep.vegetable.backend.features.user.domain.UserDomainService;
import indi.midreamsheep.vegetable.backend.features.user.domain.command.UserPasswordChangeCommand;
import indi.midreamsheep.vegetable.backend.features.user.domain.command.UserProfileUpdateCommand;
import indi.midreamsheep.vegetable.backend.features.user.domain.model.UserProfileData;
import indi.midreamsheep.vegetable.backend.features.user.presentation.dto.UserPasswordChangeRequest;
import indi.midreamsheep.vegetable.backend.features.user.presentation.dto.UserProfileResponse;
import indi.midreamsheep.vegetable.backend.features.user.presentation.dto.UserProfileUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户接口。
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserDomainService userDomainService;

    /**
     * 构造用户控制器。
     *
     * @param userDomainService 用户领域服务
     */
    public UserController(UserDomainService userDomainService) {
        this.userDomainService = userDomainService;
    }

    /**
     * 获取当前用户资料。
     *
     * @return 统一响应体（用户资料）
     */
    @GetMapping("/me")
    public ApiResponse<UserProfileResponse> me() {
        long userId = requireCurrentUserId();
        UserProfileData profile = userDomainService.getProfile(userId);
        return ApiResponse.ok(toProfileResponse(profile));
    }

    /**
     * 更新当前用户资料。
     *
     * @param request 更新请求
     * @return 统一响应体（用户资料）
     */
    @PutMapping("/me")
    public ApiResponse<UserProfileResponse> updateProfile(@Valid @RequestBody UserProfileUpdateRequest request) {
        long userId = requireCurrentUserId();
        UserProfileData profile = userDomainService.updateProfile(new UserProfileUpdateCommand(
                userId,
                request.nickname(),
                request.avatarFileId()
        ));
        return ApiResponse.ok(toProfileResponse(profile));
    }

    /**
     * 修改当前用户密码。
     *
     * @param request 修改请求
     * @return 统一响应体
     */
    @PutMapping("/me/password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody UserPasswordChangeRequest request) {
        long userId = requireCurrentUserId();
        userDomainService.changePassword(new UserPasswordChangeCommand(
                userId,
                request.oldPassword(),
                request.newPassword()
        ));
        return ApiResponse.ok();
    }

    /**
     * 获取当前用户 ID。
     *
     * @return 用户ID
     */
    private static long requireCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "未登录");
        }
        Object principalObj = authentication.getPrincipal();
        String principal = (principalObj instanceof UserDetails userDetails)
                ? userDetails.getUsername()
                : String.valueOf(principalObj);
        try {
            long userId = Long.parseLong(principal);
            if (userId <= 0) {
                throw new NumberFormatException("id <= 0");
            }
            return userId;
        } catch (NumberFormatException ex) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "未登录");
        }
    }

    /**
     * 转换为用户资料响应。
     *
     * @param data 用户资料
     * @return 响应 DTO
     */
    private static UserProfileResponse toProfileResponse(UserProfileData data) {
        return new UserProfileResponse(
                data.id(),
                data.username(),
                data.nickname(),
                data.avatarFileId(),
                data.status()
        );
    }
}

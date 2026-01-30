package indi.midreamsheep.vegetable.backend.features.user.presentation;

import indi.midreamsheep.vegetable.backend.common.api.ApiResponse;
import indi.midreamsheep.vegetable.backend.common.api.PageResponse;
import indi.midreamsheep.vegetable.backend.features.user.domain.UserDomainService;
import indi.midreamsheep.vegetable.backend.features.user.domain.UserStatus;
import indi.midreamsheep.vegetable.backend.features.user.domain.command.UserPasswordResetCommand;
import indi.midreamsheep.vegetable.backend.features.user.domain.command.UserStatusUpdateCommand;
import indi.midreamsheep.vegetable.backend.features.user.domain.model.UserAdminDetailData;
import indi.midreamsheep.vegetable.backend.features.user.domain.model.UserProfileData;
import indi.midreamsheep.vegetable.backend.features.user.domain.model.UserSummaryData;
import indi.midreamsheep.vegetable.backend.features.user.domain.query.UserQuery;
import indi.midreamsheep.vegetable.backend.features.role.domain.model.RoleData;
import indi.midreamsheep.vegetable.backend.features.role.presentation.dto.RoleResponse;
import indi.midreamsheep.vegetable.backend.features.user.presentation.dto.UserAdminDetailResponse;
import indi.midreamsheep.vegetable.backend.features.user.presentation.dto.UserProfileResponse;
import indi.midreamsheep.vegetable.backend.features.user.presentation.dto.UserPasswordResetRequest;
import indi.midreamsheep.vegetable.backend.features.user.presentation.dto.UserRolesUpdateRequest;
import indi.midreamsheep.vegetable.backend.features.user.presentation.dto.UserStatusUpdateRequest;
import indi.midreamsheep.vegetable.backend.features.user.presentation.dto.UserSummaryResponse;
import indi.midreamsheep.vegetable.backend.infrastructure.security.AdminAuthorizationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端用户接口。
 */
@RestController
@RequestMapping("/api/v1/admin/users")
public class UserAdminController {

    private final UserDomainService userDomainService;
    private final AdminAuthorizationService adminAuthorizationService;

    /**
     * 构造管理端用户控制器。
     *
     * @param userDomainService 用户领域服务
     * @param adminAuthorizationService 管理员权限服务
     */
    public UserAdminController(
            UserDomainService userDomainService,
            AdminAuthorizationService adminAuthorizationService
    ) {
        this.userDomainService = userDomainService;
        this.adminAuthorizationService = adminAuthorizationService;
    }

    /**
     * 管理端用户列表。
     *
     * @param keyword 关键字
     * @param status 状态
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @GetMapping
    public ApiResponse<PageResponse<UserSummaryResponse>> list(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) UserStatus status,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize
    ) {
        adminAuthorizationService.requireAdmin();
        int finalPage = normalizePage(page);
        int finalPageSize = normalizePageSize(pageSize);
        UserQuery query = new UserQuery(keyword, status, finalPage, finalPageSize);
        PageResponse<UserSummaryData> result = userDomainService.list(query);
        List<UserSummaryResponse> items = result.items().stream()
                .map(UserAdminController::toSummaryResponse)
                .toList();
        return ApiResponse.ok(new PageResponse<>(items, result.page(), result.pageSize(), result.total()));
    }

    /**
     * 获取用户详情。
     *
     * @param id 用户ID
     * @return 用户详情
     */
    @GetMapping("/{id}")
    public ApiResponse<UserAdminDetailResponse> detail(@PathVariable("id") long id) {
        adminAuthorizationService.requireAdmin();
        UserAdminDetailData profile = userDomainService.getAdminDetail(id);
        List<RoleResponse> roles = userDomainService.listRoles(id).stream()
                .map(UserAdminController::toRoleResponse)
                .toList();
        return ApiResponse.ok(new UserAdminDetailResponse(
                profile.id(),
                profile.username(),
                profile.nickname(),
                profile.avatarFileId(),
                profile.status(),
                profile.lastLoginAt(),
                profile.createdAt(),
                roles
        ));
    }

    /**
     * 重置用户密码。
     *
     * @param id 用户ID
     * @param request 请求体
     * @return 统一响应体
     */
    @PutMapping("/{id}/reset-password")
    public ApiResponse<Void> resetPassword(
            @PathVariable("id") long id,
            @Valid @RequestBody UserPasswordResetRequest request
    ) {
        adminAuthorizationService.requireAdmin();
        userDomainService.resetPassword(new UserPasswordResetCommand(id, request.newPassword()));
        return ApiResponse.ok();
    }

    /**
     * 更新用户角色。
     *
     * @param id 用户ID
     * @param request 请求体
     * @return 统一响应体
     */
    @PutMapping("/{id}/roles")
    public ApiResponse<Void> updateRoles(
            @PathVariable("id") long id,
            @Valid @RequestBody UserRolesUpdateRequest request
    ) {
        adminAuthorizationService.requireAdmin();
        userDomainService.updateRoles(id, request.roleIds());
        return ApiResponse.ok();
    }

    /**
     * 获取用户角色列表。
     *
     * @param id 用户ID
     * @return 角色列表
     */
    @GetMapping("/{id}/roles")
    public ApiResponse<List<RoleResponse>> listRoles(@PathVariable("id") long id) {
        adminAuthorizationService.requireAdmin();
        List<RoleResponse> roles = userDomainService.listRoles(id).stream()
                .map(UserAdminController::toRoleResponse)
                .toList();
        return ApiResponse.ok(roles);
    }

    /**
     * 删除用户（软删除）。
     *
     * @param id 用户ID
     * @return 统一响应体
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") long id) {
        adminAuthorizationService.requireAdmin();
        userDomainService.delete(id);
        return ApiResponse.ok();
    }

    /**
     * 更新用户状态。
     *
     * @param id 用户ID
     * @param request 更新请求
     * @return 更新后的用户资料
     */
    @PutMapping("/{id}/status")
    public ApiResponse<UserProfileResponse> updateStatus(
            @PathVariable("id") long id,
            @Valid @RequestBody UserStatusUpdateRequest request
    ) {
        adminAuthorizationService.requireAdmin();
        UserStatusUpdateCommand command = new UserStatusUpdateCommand(id, request.status());
        UserProfileData updated = userDomainService.updateStatus(command);
        return ApiResponse.ok(toProfileResponse(updated));
    }

    /**
     * 规范化页码。
     *
     * @param page 页码
     * @return 规范化后的页码
     */
    private static int normalizePage(Integer page) {
        if (page == null || page < 1) {
            return 1;
        }
        return page;
    }

    /**
     * 规范化每页大小。
     *
     * @param pageSize 每页大小
     * @return 规范化后的每页大小
     */
    private static int normalizePageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return 20;
        }
        return Math.min(pageSize, 100);
    }

    /**
     * 转换为用户摘要响应。
     *
     * @param data 用户摘要
     * @return 响应 DTO
     */
    private static UserSummaryResponse toSummaryResponse(UserSummaryData data) {
        return new UserSummaryResponse(
                data.id(),
                data.username(),
                data.nickname(),
                data.status(),
                data.lastLoginAt(),
                data.createdAt()
        );
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

    /**
     * 转换为角色响应。
     *
     * @param data 角色数据
     * @return 角色响应
     */
    private static RoleResponse toRoleResponse(RoleData data) {
        return new RoleResponse(data.id(), data.code(), data.name());
    }
}

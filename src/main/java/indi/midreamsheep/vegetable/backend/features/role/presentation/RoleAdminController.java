package indi.midreamsheep.vegetable.backend.features.role.presentation;

import indi.midreamsheep.vegetable.backend.common.api.ApiResponse;
import indi.midreamsheep.vegetable.backend.features.role.domain.RoleDomainService;
import indi.midreamsheep.vegetable.backend.features.role.domain.command.RoleCreateCommand;
import indi.midreamsheep.vegetable.backend.features.role.domain.command.RoleUpdateCommand;
import indi.midreamsheep.vegetable.backend.features.role.domain.model.RoleData;
import indi.midreamsheep.vegetable.backend.features.role.presentation.dto.RoleCreateRequest;
import indi.midreamsheep.vegetable.backend.features.role.presentation.dto.RoleResponse;
import indi.midreamsheep.vegetable.backend.features.role.presentation.dto.RoleUpdateRequest;
import indi.midreamsheep.vegetable.backend.infrastructure.security.AdminAuthorizationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

/**
 * 管理端角色接口。
 */
@RestController
@RequestMapping("/api/v1/admin/roles")
public class RoleAdminController {

    private final RoleDomainService roleDomainService;
    private final AdminAuthorizationService adminAuthorizationService;

    /**
     * 构造角色控制器。
     *
     * @param roleDomainService 角色领域服务
     * @param adminAuthorizationService 管理员权限服务
     */
    public RoleAdminController(
            RoleDomainService roleDomainService,
            AdminAuthorizationService adminAuthorizationService
    ) {
        this.roleDomainService = roleDomainService;
        this.adminAuthorizationService = adminAuthorizationService;
    }

    /**
     * 获取角色列表。
     *
     * @return 角色列表
     */
    @GetMapping
    public ApiResponse<List<RoleResponse>> list() {
        adminAuthorizationService.requireAdmin();
        List<RoleResponse> items = roleDomainService.list().stream()
                .map(RoleAdminController::toResponse)
                .toList();
        return ApiResponse.ok(items);
    }

    /**
     * 创建角色。
     *
     * @param request 创建请求
     * @return 新增角色
     */
    @PostMapping
    public ApiResponse<RoleResponse> create(@Valid @RequestBody RoleCreateRequest request) {
        adminAuthorizationService.requireAdmin();
        String normalizedCode = normalizeCode(request.code());
        long id = roleDomainService.create(new RoleCreateCommand(normalizedCode, request.name()));
        RoleData data = new RoleData(id, normalizedCode, request.name());
        return ApiResponse.ok(toResponse(data));
    }

    /**
     * 更新角色。
     *
     * @param id 角色ID
     * @param request 更新请求
     * @return 更新后的角色
     */
    @PutMapping("/{id}")
    public ApiResponse<RoleResponse> update(
            @PathVariable("id") long id,
            @Valid @RequestBody RoleUpdateRequest request
    ) {
        adminAuthorizationService.requireAdmin();
        RoleData data = roleDomainService.update(new RoleUpdateCommand(id, request.name()));
        return ApiResponse.ok(toResponse(data));
    }

    /**
     * 删除角色。
     *
     * @param id 角色ID
     * @return 统一响应体
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") long id) {
        adminAuthorizationService.requireAdmin();
        roleDomainService.delete(id);
        return ApiResponse.ok();
    }

    /**
     * 转换为角色响应 DTO。
     *
     * @param data 角色数据
     * @return 角色响应
     */
    private static RoleResponse toResponse(RoleData data) {
        return new RoleResponse(data.id(), data.code(), data.name());
    }

    /**
     * 规范化角色编码。
     *
     * @param code 角色编码
     * @return 规范化后的编码
     */
    private static String normalizeCode(String code) {
        return code == null ? null : code.trim().toUpperCase(Locale.ROOT);
    }
}

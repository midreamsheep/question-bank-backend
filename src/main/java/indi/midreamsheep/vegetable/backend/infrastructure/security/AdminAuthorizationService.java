package indi.midreamsheep.vegetable.backend.infrastructure.security;

import indi.midreamsheep.vegetable.backend.common.error.BizException;
import indi.midreamsheep.vegetable.backend.common.error.ErrorCode;
import indi.midreamsheep.vegetable.backend.features.role.domain.port.RoleRepositoryPort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 管理员权限校验服务。
 */
@Component
public class AdminAuthorizationService {

    private final AdminProperties adminProperties;
    private final RoleRepositoryPort roleRepositoryPort;

    /**
     * 构造管理员权限校验服务。
     *
     * @param adminProperties 管理员配置
     */
    public AdminAuthorizationService(AdminProperties adminProperties, RoleRepositoryPort roleRepositoryPort) {
        this.adminProperties = adminProperties;
        this.roleRepositoryPort = roleRepositoryPort;
    }

    /**
     * 校验当前用户是否为管理员并返回用户ID。
     *
     * @return 用户ID
     */
    public long requireAdminUserId() {
        Long userId = currentUserIdOrNull();
        if (userId == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "未认证用户");
        }
        List<Long> adminUserIds = adminProperties.userIds();
        boolean isAdminByConfig = adminUserIds != null && adminUserIds.contains(userId);
        boolean isAdminByRole = roleRepositoryPort.hasRole(userId, "ADMIN");
        if (!isAdminByConfig && !isAdminByRole) {
            throw new BizException(ErrorCode.FORBIDDEN, "需要管理员权限");
        }
        return userId;
    }

    /**
     * 校验当前用户是否为管理员。
     */
    public void requireAdmin() {
        requireAdminUserId();
    }

    /**
     * 获取当前用户 ID（未登录返回 null）。
     *
     * @return 用户ID 或 null
     */
    private static Long currentUserIdOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }
        Object principalObj = authentication.getPrincipal();
        String principal = (principalObj instanceof UserDetails userDetails)
                ? userDetails.getUsername()
                : String.valueOf(principalObj);
        try {
            long userId = Long.parseLong(principal);
            return userId > 0 ? userId : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}

package indi.midreamsheep.vegetable.backend.features.user.domain;

import indi.midreamsheep.vegetable.backend.common.api.PageResponse;
import indi.midreamsheep.vegetable.backend.common.error.BizException;
import indi.midreamsheep.vegetable.backend.common.error.ErrorCode;
import indi.midreamsheep.vegetable.backend.features.role.domain.model.RoleData;
import indi.midreamsheep.vegetable.backend.features.role.domain.port.RoleRepositoryPort;
import indi.midreamsheep.vegetable.backend.features.user.domain.command.UserCreateCommand;
import indi.midreamsheep.vegetable.backend.features.user.domain.command.UserPasswordChangeCommand;
import indi.midreamsheep.vegetable.backend.features.user.domain.command.UserPasswordResetCommand;
import indi.midreamsheep.vegetable.backend.features.user.domain.command.UserProfileUpdateCommand;
import indi.midreamsheep.vegetable.backend.features.user.domain.command.UserRegisterCommand;
import indi.midreamsheep.vegetable.backend.features.user.domain.command.UserStatusUpdateCommand;
import indi.midreamsheep.vegetable.backend.features.user.domain.model.UserAdminDetailData;
import indi.midreamsheep.vegetable.backend.features.user.domain.model.UserCredentialData;
import indi.midreamsheep.vegetable.backend.features.user.domain.model.UserProfileData;
import indi.midreamsheep.vegetable.backend.features.user.domain.model.UserSummaryData;
import indi.midreamsheep.vegetable.backend.features.user.domain.port.UserRepositoryPort;
import indi.midreamsheep.vegetable.backend.features.user.domain.query.UserQuery;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 用户领域服务。
 */
public class UserDomainService {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepositoryPort roleRepositoryPort;

    /**
     * 构造用户领域服务。
     *
     * @param userRepositoryPort 用户仓储端口
     * @param passwordEncoder 密码编码器
     */
    public UserDomainService(
            UserRepositoryPort userRepositoryPort,
            PasswordEncoder passwordEncoder,
            RoleRepositoryPort roleRepositoryPort
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.roleRepositoryPort = roleRepositoryPort;
    }

    /**
     * 注册用户。
     *
     * @param command 注册命令
     * @return 用户资料
     */
    public UserProfileData register(UserRegisterCommand command) {
        validate(command);
        String username = command.username().trim();
        String nickname = StringUtils.hasText(command.nickname()) ? command.nickname().trim() : username;
        userRepositoryPort.findProfileByUsername(username).ifPresent(existing -> {
            throw new BizException(ErrorCode.BAD_REQUEST, "用户名已存在");
        });
        String passwordHash = passwordEncoder.encode(command.password());
        UserCreateCommand createCommand = new UserCreateCommand(
                username,
                passwordHash,
                nickname,
                null,
                UserStatus.ACTIVE
        );
        long id = userRepositoryPort.create(createCommand);
        assignDefaultRole(id);
        return new UserProfileData(id, username, nickname, null, UserStatus.ACTIVE);
    }

    /**
     * 获取用户资料。
     *
     * @param userId 用户ID
     * @return 用户资料
     */
    public UserProfileData getProfile(long userId) {
        return userRepositoryPort.findProfileById(userId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "用户不存在"));
    }

    /**
     * 获取管理端用户详情。
     *
     * @param userId 用户ID
     * @return 用户详情
     */
    public UserAdminDetailData getAdminDetail(long userId) {
        return userRepositoryPort.findAdminDetailById(userId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "用户不存在"));
    }

    /**
     * 管理端分页查询用户。
     *
     * @param query 查询条件
     * @return 分页结果
     */
    public PageResponse<UserSummaryData> list(UserQuery query) {
        return userRepositoryPort.list(query);
    }

    /**
     * 更新用户状态。
     *
     * @param command 更新命令
     * @return 更新后的资料
     */
    public UserProfileData updateStatus(UserStatusUpdateCommand command) {
        validate(command);
        UserProfileData existing = userRepositoryPort.findProfileById(command.id())
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "用户不存在"));
        if (existing.status() == command.status()) {
            return existing;
        }
        userRepositoryPort.updateStatus(command);
        return userRepositoryPort.findProfileById(command.id())
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "用户不存在"));
    }

    /**
     * 更新用户资料。
     *
     * @param command 更新命令
     * @return 更新后的资料
     */
    public UserProfileData updateProfile(UserProfileUpdateCommand command) {
        validate(command);
        UserProfileData existing = userRepositoryPort.findProfileById(command.id())
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "用户不存在"));
        String nickname = command.nickname() == null ? existing.nickname() : command.nickname().trim();
        Long avatarFileId = command.avatarFileId() == null ? existing.avatarFileId() : command.avatarFileId();
        userRepositoryPort.updateProfile(command.id(), nickname, avatarFileId);
        return userRepositoryPort.findProfileById(command.id())
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "用户不存在"));
    }

    /**
     * 用户修改密码。
     *
     * @param command 修改命令
     */
    public void changePassword(UserPasswordChangeCommand command) {
        validate(command);
        UserCredentialData credential = userRepositoryPort.findCredentialById(command.id())
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "用户不存在"));
        if (!passwordEncoder.matches(command.oldPassword(), credential.passwordHash())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "旧密码不正确");
        }
        String newHash = passwordEncoder.encode(command.newPassword());
        userRepositoryPort.updatePassword(command.id(), newHash);
    }

    /**
     * 管理员重置用户密码。
     *
     * @param command 重置命令
     */
    public void resetPassword(UserPasswordResetCommand command) {
        validate(command);
        userRepositoryPort.findProfileById(command.id())
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "用户不存在"));
        String newHash = passwordEncoder.encode(command.newPassword());
        userRepositoryPort.updatePassword(command.id(), newHash);
    }

    /**
     * 软删除用户。
     *
     * @param userId 用户ID
     */
    public void delete(long userId) {
        if (userId <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "id 不合法");
        }
        userRepositoryPort.findProfileById(userId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "用户不存在"));
        userRepositoryPort.softDelete(userId);
    }

    /**
     * 获取用户角色列表。
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    public List<RoleData> listRoles(long userId) {
        if (userId <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "id 不合法");
        }
        userRepositoryPort.findProfileById(userId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "用户不存在"));
        return roleRepositoryPort.listByUserId(userId);
    }

    /**
     * 更新用户角色。
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    public void updateRoles(long userId, List<Long> roleIds) {
        if (userId <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "id 不合法");
        }
        userRepositoryPort.findProfileById(userId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "用户不存在"));
        validateRoleIds(roleIds);
        validateRoleExists(roleIds);
        roleRepositoryPort.replaceUserRoles(userId, roleIds);
    }

    /**
     * 校验注册命令。
     *
     * @param command 注册命令
     */
    private static void validate(UserRegisterCommand command) {
        if (command == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "请求不能为空");
        }
        if (!StringUtils.hasText(command.username())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "用户名不能为空");
        }
        if (!StringUtils.hasText(command.password())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "密码不能为空");
        }
        if (command.username().trim().length() > 64) {
            throw new BizException(ErrorCode.BAD_REQUEST, "用户名过长");
        }
        if (command.password().length() < 6 || command.password().length() > 64) {
            throw new BizException(ErrorCode.BAD_REQUEST, "密码长度需为 6-64 位");
        }
        if (StringUtils.hasText(command.nickname()) && command.nickname().trim().length() > 64) {
            throw new BizException(ErrorCode.BAD_REQUEST, "昵称过长");
        }
    }

    /**
     * 校验状态更新命令。
     *
     * @param command 更新命令
     */
    private static void validate(UserStatusUpdateCommand command) {
        if (command == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "请求不能为空");
        }
        if (command.id() <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "id 不合法");
        }
        if (command.status() == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "status 不能为空");
        }
    }

    /**
     * 校验资料更新命令。
     *
     * @param command 更新命令
     */
    private static void validate(UserProfileUpdateCommand command) {
        if (command == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "请求不能为空");
        }
        if (command.id() <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "id 不合法");
        }
        if (command.nickname() != null && !StringUtils.hasText(command.nickname())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "昵称不能为空");
        }
        if (command.nickname() != null && command.nickname().trim().length() > 64) {
            throw new BizException(ErrorCode.BAD_REQUEST, "昵称过长");
        }
        if (command.avatarFileId() != null && command.avatarFileId() <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "avatarFileId 不合法");
        }
    }

    /**
     * 校验修改密码命令。
     *
     * @param command 修改命令
     */
    private static void validate(UserPasswordChangeCommand command) {
        if (command == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "请求不能为空");
        }
        if (command.id() <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "id 不合法");
        }
        if (!StringUtils.hasText(command.oldPassword())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "旧密码不能为空");
        }
        if (!StringUtils.hasText(command.newPassword())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "新密码不能为空");
        }
        if (command.newPassword().length() < 6 || command.newPassword().length() > 64) {
            throw new BizException(ErrorCode.BAD_REQUEST, "密码长度需为 6-64 位");
        }
    }

    /**
     * 校验重置密码命令。
     *
     * @param command 重置命令
     */
    private static void validate(UserPasswordResetCommand command) {
        if (command == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "请求不能为空");
        }
        if (command.id() <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "id 不合法");
        }
        if (!StringUtils.hasText(command.newPassword())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "新密码不能为空");
        }
        if (command.newPassword().length() < 6 || command.newPassword().length() > 64) {
            throw new BizException(ErrorCode.BAD_REQUEST, "密码长度需为 6-64 位");
        }
    }

    /**
     * 校验角色ID列表。
     *
     * @param roleIds 角色ID列表
     */
    private static void validateRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        java.util.Set<Long> unique = new java.util.HashSet<>();
        for (Long roleId : roleIds) {
            if (roleId == null || roleId <= 0) {
                throw new BizException(ErrorCode.BAD_REQUEST, "roleIds 包含不合法的 ID");
            }
            if (!unique.add(roleId)) {
                throw new BizException(ErrorCode.BAD_REQUEST, "roleIds 包含重复 ID");
            }
        }
    }

    /**
     * 校验角色是否存在。
     *
     * @param roleIds 角色ID列表
     */
    private void validateRoleExists(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        List<RoleData> roles = roleRepositoryPort.list();
        java.util.Set<Long> existing = new java.util.HashSet<>();
        for (RoleData role : roles) {
            existing.add(role.id());
        }
        for (Long roleId : roleIds) {
            if (!existing.contains(roleId)) {
                throw new BizException(ErrorCode.BAD_REQUEST, "roleId 不存在");
            }
        }
    }

    /**
     * 注册完成后分配默认角色。
     *
     * @param userId 用户ID
     */
    private void assignDefaultRole(long userId) {
        roleRepositoryPort.findByCode("USER").ifPresent(role -> {
            roleRepositoryPort.replaceUserRoles(userId, List.of(role.id()));
        });
    }
}

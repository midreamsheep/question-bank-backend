package indi.midreamsheep.vegetable.backend.features.user.domain.port;

import indi.midreamsheep.vegetable.backend.common.api.PageResponse;
import indi.midreamsheep.vegetable.backend.features.user.domain.command.UserCreateCommand;
import indi.midreamsheep.vegetable.backend.features.user.domain.command.UserStatusUpdateCommand;
import indi.midreamsheep.vegetable.backend.features.user.domain.model.UserAdminDetailData;
import indi.midreamsheep.vegetable.backend.features.user.domain.model.UserCredentialData;
import indi.midreamsheep.vegetable.backend.features.user.domain.model.UserProfileData;
import indi.midreamsheep.vegetable.backend.features.user.domain.model.UserSummaryData;
import indi.midreamsheep.vegetable.backend.features.user.domain.query.UserQuery;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 用户仓储端口。
 */
public interface UserRepositoryPort {

    /**
     * 根据 ID 获取用户资料。
     *
     * @param id 用户ID
     * @return 用户资料
     */
    Optional<UserProfileData> findProfileById(long id);

    /**
     * 管理端获取用户详情。
     *
     * @param id 用户ID
     * @return 用户详情
     */
    Optional<UserAdminDetailData> findAdminDetailById(long id);

    /**
     * 根据用户名获取用户资料。
     *
     * @param username 用户名
     * @return 用户资料
     */
    Optional<UserProfileData> findProfileByUsername(String username);

    /**
     * 根据用户名获取用户凭证信息。
     *
     * @param username 用户名
     * @return 凭证信息
     */
    Optional<UserCredentialData> findCredentialByUsername(String username);

    /**
     * 根据用户ID获取凭证信息。
     *
     * @param id 用户ID
     * @return 凭证信息
     */
    Optional<UserCredentialData> findCredentialById(long id);

    /**
     * 创建用户并返回用户ID。
     *
     * @param command 创建命令
     * @return 用户ID
     */
    long create(UserCreateCommand command);

    /**
     * 更新用户状态。
     *
     * @param command 更新命令
     */
    void updateStatus(UserStatusUpdateCommand command);

    /**
     * 更新用户资料。
     *
     * @param id 用户ID
     * @param nickname 昵称
     * @param avatarFileId 头像文件ID
     */
    void updateProfile(long id, String nickname, Long avatarFileId);

    /**
     * 更新用户密码。
     *
     * @param id 用户ID
     * @param passwordHash 密码哈希
     */
    void updatePassword(long id, String passwordHash);

    /**
     * 软删除用户。
     *
     * @param id 用户ID
     */
    void softDelete(long id);

    /**
     * 更新最近登录时间。
     *
     * @param userId 用户ID
     * @param loginAt 登录时间
     */
    void updateLastLogin(long userId, LocalDateTime loginAt);

    /**
     * 管理端分页查询用户。
     *
     * @param query 查询条件
     * @return 分页结果
     */
    PageResponse<UserSummaryData> list(UserQuery query);
}

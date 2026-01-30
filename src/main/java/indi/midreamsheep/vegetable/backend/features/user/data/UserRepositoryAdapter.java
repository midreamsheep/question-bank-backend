package indi.midreamsheep.vegetable.backend.features.user.data;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import indi.midreamsheep.vegetable.backend.common.api.PageResponse;
import indi.midreamsheep.vegetable.backend.features.user.domain.UserStatus;
import indi.midreamsheep.vegetable.backend.features.user.domain.command.UserCreateCommand;
import indi.midreamsheep.vegetable.backend.features.user.domain.command.UserStatusUpdateCommand;
import indi.midreamsheep.vegetable.backend.features.user.domain.model.UserAdminDetailData;
import indi.midreamsheep.vegetable.backend.features.user.domain.model.UserCredentialData;
import indi.midreamsheep.vegetable.backend.features.user.domain.model.UserProfileData;
import indi.midreamsheep.vegetable.backend.features.user.domain.model.UserSummaryData;
import indi.midreamsheep.vegetable.backend.features.user.domain.port.UserRepositoryPort;
import indi.midreamsheep.vegetable.backend.features.user.domain.query.UserQuery;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.UserEntity;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper.UserMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户仓储适配器：通过 MyBatis-Plus 完成持久化。
 */
@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserMapper userMapper;

    /**
     * 构造用户仓储适配器。
     *
     * @param userMapper 用户 Mapper
     */
    public UserRepositoryAdapter(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public Optional<UserProfileData> findProfileById(long id) {
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id)
                .eq("deleted", 0);
        return Optional.ofNullable(userMapper.selectOne(wrapper))
                .map(UserRepositoryAdapter::toProfileData);
    }

    @Override
    public Optional<UserAdminDetailData> findAdminDetailById(long id) {
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id)
                .eq("deleted", 0);
        return Optional.ofNullable(userMapper.selectOne(wrapper))
                .map(UserRepositoryAdapter::toAdminDetailData);
    }

    @Override
    public Optional<UserProfileData> findProfileByUsername(String username) {
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username)
                .eq("deleted", 0);
        return Optional.ofNullable(userMapper.selectOne(wrapper))
                .map(UserRepositoryAdapter::toProfileData);
    }

    @Override
    public Optional<UserCredentialData> findCredentialByUsername(String username) {
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username)
                .eq("deleted", 0);
        return Optional.ofNullable(userMapper.selectOne(wrapper))
                .map(UserRepositoryAdapter::toCredentialData);
    }

    @Override
    public Optional<UserCredentialData> findCredentialById(long id) {
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id)
                .eq("deleted", 0);
        return Optional.ofNullable(userMapper.selectOne(wrapper))
                .map(UserRepositoryAdapter::toCredentialData);
    }

    @Override
    public long create(UserCreateCommand command) {
        LocalDateTime now = LocalDateTime.now();
        UserEntity entity = new UserEntity();
        entity.setUsername(command.username());
        entity.setPasswordHash(command.passwordHash());
        entity.setNickname(command.nickname());
        entity.setAvatarFileId(command.avatarFileId());
        entity.setStatus(command.status().name());
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setDeleted(0);
        userMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public void updateStatus(UserStatusUpdateCommand command) {
        LocalDateTime now = LocalDateTime.now();
        UpdateWrapper<UserEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", command.id())
                .set("status", command.status().name())
                .set("updated_at", now);
        userMapper.update(null, wrapper);
    }

    @Override
    public void updateProfile(long id, String nickname, Long avatarFileId) {
        LocalDateTime now = LocalDateTime.now();
        UpdateWrapper<UserEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id)
                .set("nickname", nickname)
                .set("avatar_file_id", avatarFileId)
                .set("updated_at", now);
        userMapper.update(null, wrapper);
    }

    @Override
    public void updatePassword(long id, String passwordHash) {
        LocalDateTime now = LocalDateTime.now();
        UpdateWrapper<UserEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id)
                .set("password_hash", passwordHash)
                .set("updated_at", now);
        userMapper.update(null, wrapper);
    }

    @Override
    public void softDelete(long id) {
        LocalDateTime now = LocalDateTime.now();
        UpdateWrapper<UserEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id)
                .set("deleted", 1)
                .set("status", UserStatus.DISABLED.name())
                .set("updated_at", now);
        userMapper.update(null, wrapper);
    }

    @Override
    public void updateLastLogin(long userId, LocalDateTime loginAt) {
        UpdateWrapper<UserEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", userId)
                .set("last_login_at", loginAt)
                .set("updated_at", loginAt);
        userMapper.update(null, wrapper);
    }

    @Override
    public PageResponse<UserSummaryData> list(UserQuery query) {
        QueryWrapper<UserEntity> countWrapper = buildQueryWrapper(query);
        long total = userMapper.selectCount(countWrapper);

        QueryWrapper<UserEntity> listWrapper = buildQueryWrapper(query);
        listWrapper.select("id", "username", "nickname", "status", "last_login_at", "created_at");
        listWrapper.orderByDesc("created_at");
        int offset = Math.max(0, (query.page() - 1) * query.pageSize());
        listWrapper.last("limit " + offset + ", " + query.pageSize());

        List<UserSummaryData> items = userMapper.selectList(listWrapper).stream()
                .map(UserRepositoryAdapter::toSummaryData)
                .toList();
        return new PageResponse<>(items, query.page(), query.pageSize(), total);
    }

    /**
     * 构造用户查询条件。
     *
     * @param query 查询参数
     * @return 查询包装器
     */
    private static QueryWrapper<UserEntity> buildQueryWrapper(UserQuery query) {
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0);
        if (query.status() != null) {
            wrapper.eq("status", query.status().name());
        }
        if (StringUtils.hasText(query.keyword())) {
            String keyword = query.keyword().trim();
            wrapper.and(w -> w.like("username", keyword).or().like("nickname", keyword));
        }
        return wrapper;
    }

    /**
     * 转换为用户资料数据。
     *
     * @param entity 用户实体
     * @return 用户资料
     */
    private static UserProfileData toProfileData(UserEntity entity) {
        UserStatus status = entity.getStatus() == null ? null : UserStatus.valueOf(entity.getStatus());
        return new UserProfileData(
                entity.getId(),
                entity.getUsername(),
                entity.getNickname(),
                entity.getAvatarFileId(),
                status
        );
    }

    /**
     * 转换为凭证数据。
     *
     * @param entity 用户实体
     * @return 凭证数据
     */
    private static UserCredentialData toCredentialData(UserEntity entity) {
        UserStatus status = entity.getStatus() == null ? null : UserStatus.valueOf(entity.getStatus());
        return new UserCredentialData(
                entity.getId(),
                entity.getUsername(),
                entity.getPasswordHash(),
                status
        );
    }

    /**
     * 转换为用户摘要数据。
     *
     * @param entity 用户实体
     * @return 用户摘要
     */
    private static UserSummaryData toSummaryData(UserEntity entity) {
        UserStatus status = entity.getStatus() == null ? null : UserStatus.valueOf(entity.getStatus());
        return new UserSummaryData(
                entity.getId(),
                entity.getUsername(),
                entity.getNickname(),
                status,
                entity.getLastLoginAt(),
                entity.getCreatedAt()
        );
    }

    /**
     * 转换为管理端详情数据。
     *
     * @param entity 用户实体
     * @return 详情数据
     */
    private static UserAdminDetailData toAdminDetailData(UserEntity entity) {
        UserStatus status = entity.getStatus() == null ? null : UserStatus.valueOf(entity.getStatus());
        return new UserAdminDetailData(
                entity.getId(),
                entity.getUsername(),
                entity.getNickname(),
                entity.getAvatarFileId(),
                status,
                entity.getLastLoginAt(),
                entity.getCreatedAt()
        );
    }
}

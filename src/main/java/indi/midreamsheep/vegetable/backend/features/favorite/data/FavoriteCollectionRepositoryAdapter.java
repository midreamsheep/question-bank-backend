package indi.midreamsheep.vegetable.backend.features.favorite.data;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import indi.midreamsheep.vegetable.backend.common.api.PageResponse;
import indi.midreamsheep.vegetable.backend.features.favorite.domain.port.FavoriteCollectionRepositoryPort;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.UserFavoriteCollectionEntity;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper.UserFavoriteCollectionMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户收藏题单仓储适配器。
 */
@Component
public class FavoriteCollectionRepositoryAdapter implements FavoriteCollectionRepositoryPort {

    private final UserFavoriteCollectionMapper userFavoriteCollectionMapper;

    /**
     * 构造收藏题单仓储适配器。
     *
     * @param userFavoriteCollectionMapper 收藏题单 Mapper
     */
    public FavoriteCollectionRepositoryAdapter(UserFavoriteCollectionMapper userFavoriteCollectionMapper) {
        this.userFavoriteCollectionMapper = userFavoriteCollectionMapper;
    }

    /**
     * 收藏题单（幂等）。
     *
     * @param userId 用户ID
     * @param collectionId 题单ID
     * @return 是否发生状态变更
     */
    @Override
    public boolean add(long userId, long collectionId) {
        QueryWrapper<UserFavoriteCollectionEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("collection_id", collectionId);
        UserFavoriteCollectionEntity existing = userFavoriteCollectionMapper.selectOne(wrapper);
        LocalDateTime now = LocalDateTime.now();
        if (existing == null) {
            UserFavoriteCollectionEntity entity = new UserFavoriteCollectionEntity();
            entity.setUserId(userId);
            entity.setCollectionId(collectionId);
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);
            entity.setDeleted(0);
            userFavoriteCollectionMapper.insert(entity);
            return true;
        }
        if (existing.getDeleted() != null && existing.getDeleted() == 0) {
            return false;
        }
        UpdateWrapper<UserFavoriteCollectionEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", existing.getId())
                .set("deleted", 0)
                .set("created_at", now)
                .set("updated_at", now);
        return userFavoriteCollectionMapper.update(null, updateWrapper) > 0;
    }

    /**
     * 取消收藏（幂等）。
     *
     * @param userId 用户ID
     * @param collectionId 题单ID
     * @return 是否发生状态变更
     */
    @Override
    public boolean remove(long userId, long collectionId) {
        LocalDateTime now = LocalDateTime.now();
        UpdateWrapper<UserFavoriteCollectionEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId)
                .eq("collection_id", collectionId)
                .eq("deleted", 0)
                .set("deleted", 1)
                .set("updated_at", now);
        return userFavoriteCollectionMapper.update(null, updateWrapper) > 0;
    }

    /**
     * 收藏题单 ID 列表（分页）。
     *
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果（items 为 collectionId）
     */
    @Override
    public PageResponse<Long> listCollectionIds(long userId, int page, int pageSize) {
        QueryWrapper<UserFavoriteCollectionEntity> countWrapper = new QueryWrapper<>();
        countWrapper.eq("user_id", userId).eq("deleted", 0);
        long total = userFavoriteCollectionMapper.selectCount(countWrapper);

        QueryWrapper<UserFavoriteCollectionEntity> listWrapper = new QueryWrapper<>();
        listWrapper.eq("user_id", userId).eq("deleted", 0);
        listWrapper.select("collection_id", "created_at");
        listWrapper.orderByDesc("created_at").orderByDesc("id");
        int offset = Math.max(0, (page - 1) * pageSize);
        listWrapper.last("limit " + offset + ", " + pageSize);
        List<Long> ids = userFavoriteCollectionMapper.selectList(listWrapper).stream()
                .map(e -> e.getCollectionId() == null ? 0L : e.getCollectionId())
                .filter(id -> id > 0)
                .toList();
        return new PageResponse<>(ids, page, pageSize, total);
    }
}

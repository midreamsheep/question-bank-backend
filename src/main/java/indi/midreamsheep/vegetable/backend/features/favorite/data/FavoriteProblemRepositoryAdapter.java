package indi.midreamsheep.vegetable.backend.features.favorite.data;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import indi.midreamsheep.vegetable.backend.common.api.PageResponse;
import indi.midreamsheep.vegetable.backend.features.favorite.domain.port.FavoriteProblemRepositoryPort;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.UserFavoriteProblemEntity;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper.UserFavoriteProblemMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户收藏题目仓储适配器。
 */
@Component
public class FavoriteProblemRepositoryAdapter implements FavoriteProblemRepositoryPort {

    private final UserFavoriteProblemMapper userFavoriteProblemMapper;

    /**
     * 构造收藏题目仓储适配器。
     *
     * @param userFavoriteProblemMapper 收藏题目 Mapper
     */
    public FavoriteProblemRepositoryAdapter(UserFavoriteProblemMapper userFavoriteProblemMapper) {
        this.userFavoriteProblemMapper = userFavoriteProblemMapper;
    }

    /**
     * 收藏题目（幂等）。
     *
     * @param userId 用户ID
     * @param problemId 题目ID
     * @return 是否发生状态变更
     */
    @Override
    public boolean add(long userId, long problemId) {
        QueryWrapper<UserFavoriteProblemEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("problem_id", problemId);
        UserFavoriteProblemEntity existing = userFavoriteProblemMapper.selectOne(wrapper);
        LocalDateTime now = LocalDateTime.now();
        if (existing == null) {
            UserFavoriteProblemEntity entity = new UserFavoriteProblemEntity();
            entity.setUserId(userId);
            entity.setProblemId(problemId);
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);
            entity.setDeleted(0);
            userFavoriteProblemMapper.insert(entity);
            return true;
        }
        if (existing.getDeleted() != null && existing.getDeleted() == 0) {
            return false;
        }
        UpdateWrapper<UserFavoriteProblemEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", existing.getId())
                .set("deleted", 0)
                .set("created_at", now)
                .set("updated_at", now);
        return userFavoriteProblemMapper.update(null, updateWrapper) > 0;
    }

    /**
     * 取消收藏（幂等）。
     *
     * @param userId 用户ID
     * @param problemId 题目ID
     * @return 是否发生状态变更
     */
    @Override
    public boolean remove(long userId, long problemId) {
        LocalDateTime now = LocalDateTime.now();
        UpdateWrapper<UserFavoriteProblemEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId)
                .eq("problem_id", problemId)
                .eq("deleted", 0)
                .set("deleted", 1)
                .set("updated_at", now);
        return userFavoriteProblemMapper.update(null, updateWrapper) > 0;
    }

    /**
     * 收藏题目 ID 列表（分页）。
     *
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果（items 为 problemId）
     */
    @Override
    public PageResponse<Long> listProblemIds(long userId, int page, int pageSize) {
        QueryWrapper<UserFavoriteProblemEntity> countWrapper = new QueryWrapper<>();
        countWrapper.eq("user_id", userId).eq("deleted", 0);
        long total = userFavoriteProblemMapper.selectCount(countWrapper);

        QueryWrapper<UserFavoriteProblemEntity> listWrapper = new QueryWrapper<>();
        listWrapper.eq("user_id", userId).eq("deleted", 0);
        listWrapper.select("problem_id", "created_at");
        listWrapper.orderByDesc("created_at").orderByDesc("id");
        int offset = Math.max(0, (page - 1) * pageSize);
        listWrapper.last("limit " + offset + ", " + pageSize);
        List<Long> ids = userFavoriteProblemMapper.selectList(listWrapper).stream()
                .map(e -> e.getProblemId() == null ? 0L : e.getProblemId())
                .filter(id -> id > 0)
                .toList();
        return new PageResponse<>(ids, page, pageSize, total);
    }
}

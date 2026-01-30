package indi.midreamsheep.vegetable.backend.features.like.data;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import indi.midreamsheep.vegetable.backend.common.api.PageResponse;
import indi.midreamsheep.vegetable.backend.features.like.domain.port.LikeCommentRepositoryPort;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.UserLikeCommentEntity;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper.UserLikeCommentMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户点赞评论仓储适配器。
 */
@Component
public class LikeCommentRepositoryAdapter implements LikeCommentRepositoryPort {

    private final UserLikeCommentMapper userLikeCommentMapper;

    /**
     * 构造点赞评论仓储适配器。
     *
     * @param userLikeCommentMapper 点赞评论 Mapper
     */
    public LikeCommentRepositoryAdapter(UserLikeCommentMapper userLikeCommentMapper) {
        this.userLikeCommentMapper = userLikeCommentMapper;
    }

    /**
     * 点赞评论（幂等）。
     *
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return 是否发生状态变更
     */
    @Override
    public boolean add(long userId, long commentId) {
        QueryWrapper<UserLikeCommentEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("comment_id", commentId);
        UserLikeCommentEntity existing = userLikeCommentMapper.selectOne(wrapper);
        LocalDateTime now = LocalDateTime.now();
        if (existing == null) {
            UserLikeCommentEntity entity = new UserLikeCommentEntity();
            entity.setUserId(userId);
            entity.setCommentId(commentId);
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);
            entity.setDeleted(0);
            userLikeCommentMapper.insert(entity);
            return true;
        }
        if (existing.getDeleted() != null && existing.getDeleted() == 0) {
            return false;
        }
        UpdateWrapper<UserLikeCommentEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", existing.getId())
                .set("deleted", 0)
                .set("created_at", now)
                .set("updated_at", now);
        return userLikeCommentMapper.update(null, updateWrapper) > 0;
    }

    /**
     * 取消点赞（幂等）。
     *
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return 是否发生状态变更
     */
    @Override
    public boolean remove(long userId, long commentId) {
        LocalDateTime now = LocalDateTime.now();
        UpdateWrapper<UserLikeCommentEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId)
                .eq("comment_id", commentId)
                .eq("deleted", 0)
                .set("deleted", 1)
                .set("updated_at", now);
        return userLikeCommentMapper.update(null, updateWrapper) > 0;
    }

    /**
     * 点赞评论 ID 列表（分页）。
     *
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果（items 为 commentId）
     */
    @Override
    public PageResponse<Long> listCommentIds(long userId, int page, int pageSize) {
        QueryWrapper<UserLikeCommentEntity> countWrapper = new QueryWrapper<>();
        countWrapper.eq("user_id", userId).eq("deleted", 0);
        long total = userLikeCommentMapper.selectCount(countWrapper);

        QueryWrapper<UserLikeCommentEntity> listWrapper = new QueryWrapper<>();
        listWrapper.eq("user_id", userId).eq("deleted", 0);
        listWrapper.select("comment_id", "created_at");
        listWrapper.orderByDesc("created_at").orderByDesc("id");
        int offset = Math.max(0, (page - 1) * pageSize);
        listWrapper.last("limit " + offset + ", " + pageSize);
        List<Long> ids = userLikeCommentMapper.selectList(listWrapper).stream()
                .map(e -> e.getCommentId() == null ? 0L : e.getCommentId())
                .filter(id -> id > 0)
                .toList();
        return new PageResponse<>(ids, page, pageSize, total);
    }
}


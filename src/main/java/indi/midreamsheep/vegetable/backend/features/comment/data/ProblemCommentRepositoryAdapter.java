package indi.midreamsheep.vegetable.backend.features.comment.data;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import indi.midreamsheep.vegetable.backend.common.api.PageResponse;
import indi.midreamsheep.vegetable.backend.features.comment.domain.model.ProblemCommentData;
import indi.midreamsheep.vegetable.backend.features.comment.domain.port.ProblemCommentRepositoryPort;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.ProblemCommentEntity;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper.ProblemCommentMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 题目评论仓储适配器：通过 MyBatis-Plus 完成持久化。
 */
@Component
public class ProblemCommentRepositoryAdapter implements ProblemCommentRepositoryPort {

    private final ProblemCommentMapper problemCommentMapper;

    /**
     * 构造题目评论仓储适配器。
     *
     * @param problemCommentMapper 评论 Mapper
     */
    public ProblemCommentRepositoryAdapter(ProblemCommentMapper problemCommentMapper) {
        this.problemCommentMapper = problemCommentMapper;
    }

    /**
     * 创建评论并返回评论ID。
     *
     * @param problemId 题目ID
     * @param userId 评论用户ID
     * @param parentId 父评论ID（可为空）
     * @param replyToCommentId 回复的评论ID（可为空）
     * @param content 内容
     * @return 评论ID
     */
    @Override
    public long create(long problemId, long userId, Long parentId, Long replyToCommentId, String content) {
        LocalDateTime now = LocalDateTime.now();
        ProblemCommentEntity entity = new ProblemCommentEntity();
        entity.setProblemId(problemId);
        entity.setUserId(userId);
        entity.setParentId(parentId);
        entity.setReplyToCommentId(replyToCommentId);
        entity.setContent(content);
        entity.setLikeCount(0L);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setDeleted(0);
        problemCommentMapper.insert(entity);
        return entity.getId();
    }

    /**
     * 评论列表（分页）。
     *
     * @param problemId 题目ID
     * @param parentId 父评论ID（为空表示顶层评论）
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @Override
    public PageResponse<ProblemCommentData> listByProblem(long problemId, Long parentId, int page, int pageSize) {
        QueryWrapper<ProblemCommentEntity> countWrapper = new QueryWrapper<>();
        countWrapper.eq("problem_id", problemId);
        if (parentId == null) {
            countWrapper.isNull("parent_id");
        } else {
            countWrapper.eq("parent_id", parentId);
        }
        long total = problemCommentMapper.selectCount(countWrapper);

        QueryWrapper<ProblemCommentEntity> listWrapper = new QueryWrapper<>();
        listWrapper.eq("problem_id", problemId);
        if (parentId == null) {
            listWrapper.isNull("parent_id");
        } else {
            listWrapper.eq("parent_id", parentId);
        }
        listWrapper.orderByAsc("created_at").orderByAsc("id");
        int offset = Math.max(0, (page - 1) * pageSize);
        listWrapper.last("limit " + offset + ", " + pageSize);
        List<ProblemCommentData> items = problemCommentMapper.selectList(listWrapper).stream()
                .map(ProblemCommentRepositoryAdapter::toData)
                .toList();
        return new PageResponse<>(items, page, pageSize, total);
    }

    /**
     * 根据 ID 获取评论。
     *
     * @param id 评论ID
     * @return 评论（可为空）
     */
    @Override
    public Optional<ProblemCommentData> findById(long id) {
        QueryWrapper<ProblemCommentEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id).eq("deleted", 0);
        ProblemCommentEntity entity = problemCommentMapper.selectOne(wrapper);
        return Optional.ofNullable(entity).map(ProblemCommentRepositoryAdapter::toData);
    }

    /**
     * 根据 ID 获取评论（包含已删除）。
     *
     * @param id 评论ID
     * @return 评论（可为空）
     */
    @Override
    public Optional<ProblemCommentData> findByIdIncludingDeleted(long id) {
        QueryWrapper<ProblemCommentEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id);
        ProblemCommentEntity entity = problemCommentMapper.selectOne(wrapper);
        return Optional.ofNullable(entity).map(ProblemCommentRepositoryAdapter::toData);
    }

    /**
     * 软删除评论。
     *
     * @param id 评论ID
     */
    @Override
    public void softDelete(long id) {
        LocalDateTime now = LocalDateTime.now();
        UpdateWrapper<ProblemCommentEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id).eq("deleted", 0)
                .set("deleted", 1)
                .set("updated_at", now);
        problemCommentMapper.update(null, wrapper);
    }

    /**
     * 评论点赞数 +1。
     *
     * @param id 评论ID
     */
    @Override
    public void incrementLikeCount(long id) {
        LocalDateTime now = LocalDateTime.now();
        UpdateWrapper<ProblemCommentEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id)
                .setSql("like_count = like_count + 1")
                .set("updated_at", now);
        problemCommentMapper.update(null, wrapper);
    }

    /**
     * 评论点赞数 -1（不会减到负数）。
     *
     * @param id 评论ID
     */
    @Override
    public void decrementLikeCount(long id) {
        LocalDateTime now = LocalDateTime.now();
        UpdateWrapper<ProblemCommentEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id)
                .setSql("like_count = CASE WHEN like_count > 0 THEN like_count - 1 ELSE 0 END")
                .set("updated_at", now);
        problemCommentMapper.update(null, wrapper);
    }

    /**
     * 转换为领域数据。
     *
     * @param entity 实体
     * @return 领域数据
     */
    private static ProblemCommentData toData(ProblemCommentEntity entity) {
        boolean deleted = entity.getDeleted() != null && entity.getDeleted() != 0;
        return new ProblemCommentData(
                entity.getId() == null ? 0L : entity.getId(),
                entity.getProblemId() == null ? 0L : entity.getProblemId(),
                entity.getUserId() == null ? 0L : entity.getUserId(),
                entity.getParentId(),
                entity.getReplyToCommentId(),
                deleted ? null : entity.getContent(),
                entity.getLikeCount() == null ? 0L : entity.getLikeCount(),
                deleted,
                entity.getCreatedAt()
        );
    }
}

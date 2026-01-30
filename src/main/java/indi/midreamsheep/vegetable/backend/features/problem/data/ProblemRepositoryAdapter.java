package indi.midreamsheep.vegetable.backend.features.problem.data;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import indi.midreamsheep.vegetable.backend.common.api.PageResponse;
import indi.midreamsheep.vegetable.backend.features.problem.domain.ContentFormat;
import indi.midreamsheep.vegetable.backend.features.problem.domain.ProblemStatus;
import indi.midreamsheep.vegetable.backend.features.problem.domain.Visibility;
import indi.midreamsheep.vegetable.backend.features.problem.domain.command.ProblemCreateCommand;
import indi.midreamsheep.vegetable.backend.features.problem.domain.command.ProblemUpdateCommand;
import indi.midreamsheep.vegetable.backend.features.problem.domain.model.ProblemDetailData;
import indi.midreamsheep.vegetable.backend.features.problem.domain.model.ProblemSummaryData;
import indi.midreamsheep.vegetable.backend.features.problem.domain.port.ProblemRepositoryPort;
import indi.midreamsheep.vegetable.backend.features.problem.domain.query.ProblemQuery;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.ProblemEntity;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.ProblemTagEntity;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper.ProblemMapper;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper.ProblemTagMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 题目仓储适配器：通过 MyBatis-Plus 完成持久化。
 */
@Component
public class ProblemRepositoryAdapter implements ProblemRepositoryPort {

    private final ProblemMapper problemMapper;
    private final ProblemTagMapper problemTagMapper;

    /**
     * 构造题目仓储适配器。
     *
     * @param problemMapper 题目 Mapper
     * @param problemTagMapper 题目-标签关联 Mapper
     */
    public ProblemRepositoryAdapter(
            ProblemMapper problemMapper,
            ProblemTagMapper problemTagMapper
    ) {
        this.problemMapper = problemMapper;
        this.problemTagMapper = problemTagMapper;
    }

    /**
     * 创建题目并返回题目ID。
     *
     * @param command 创建命令
     * @return 题目ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long create(ProblemCreateCommand command) {
        LocalDateTime now = LocalDateTime.now();
        ProblemEntity entity = new ProblemEntity();
        entity.setAuthorId(command.authorId());
        entity.setTitle(command.title());
        entity.setSubject(command.subject());
        entity.setDifficulty(command.difficulty());
        entity.setStatementFormat(command.statementFormat().name());
        entity.setStatementContent(command.statementContent());
        if (command.solutionFormat() != null) {
            entity.setSolutionFormat(command.solutionFormat().name());
        }
        entity.setSolutionContent(command.solutionContent());
        entity.setVisibility(command.visibility().name());
        entity.setShareKey(command.shareKey());
        entity.setStatus(ProblemStatus.DRAFT.name());
        entity.setViewCount(0L);
        entity.setFavoriteCount(0L);
        entity.setLikeCount(0L);
        entity.setDeleted(0);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setLastModifiedAt(now);

        problemMapper.insert(entity);
        replaceTags(entity.getId(), command.tagIds(), now);
        return entity.getId();
    }

    @Override
    public Optional<ProblemDetailData> findById(long id) {
        QueryWrapper<ProblemEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id)
                .eq("deleted", 0);
        return Optional.ofNullable(problemMapper.selectOne(wrapper))
                .map(entity -> toDetailData(
                        entity,
                        fetchTagIds(entity.getId())
                ));
    }

    @Override
    public Optional<ProblemDetailData> findByShareKey(String shareKey) {
        QueryWrapper<ProblemEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("share_key", shareKey)
                .eq("deleted", 0);
        return Optional.ofNullable(problemMapper.selectOne(wrapper))
                .map(entity -> toDetailData(
                        entity,
                        fetchTagIds(entity.getId())
                ));
    }

    @Override
    public PageResponse<ProblemSummaryData> listPublic(ProblemQuery query) {
        QueryWrapper<ProblemEntity> countWrapper = buildPublicListWrapper(query);
        long total = problemMapper.selectCount(countWrapper);

        QueryWrapper<ProblemEntity> listWrapper = buildPublicListWrapper(query);
        listWrapper.select("id", "title", "subject", "difficulty", "status", "visibility", "published_at", "author_id");
        if ("HOT".equals(query.sort())) {
            listWrapper.orderByDesc("favorite_count").orderByDesc("published_at");
        } else if ("DIFFICULTY".equals(query.sort())) {
            listWrapper.orderByAsc("difficulty").orderByDesc("published_at");
        } else {
            listWrapper.orderByDesc("published_at");
        }
        int offset = Math.max(0, (query.page() - 1) * query.pageSize());
        listWrapper.last("limit " + offset + ", " + query.pageSize());

        List<ProblemEntity> entities = problemMapper.selectList(listWrapper);
        Map<Long, List<Long>> tagIdsMap = fetchTagIdsByProblemIds(entities.stream().map(ProblemEntity::getId).toList());
        List<ProblemSummaryData> items = entities.stream()
                .map(entity -> toSummaryData(entity, tagIdsMap.getOrDefault(entity.getId(), List.of())))
                .toList();
        return new PageResponse<>(items, query.page(), query.pageSize(), total);
    }

    @Override
    public PageResponse<ProblemSummaryData> listByAuthor(long authorId, ProblemStatus status, int page, int pageSize) {
        QueryWrapper<ProblemEntity> countWrapper = new QueryWrapper<>();
        countWrapper.eq("author_id", authorId).eq("deleted", 0);
        if (status != null) {
            countWrapper.eq("status", status.name());
        }
        long total = problemMapper.selectCount(countWrapper);

        QueryWrapper<ProblemEntity> listWrapper = new QueryWrapper<>();
        listWrapper.eq("author_id", authorId).eq("deleted", 0);
        if (status != null) {
            listWrapper.eq("status", status.name());
        }
        listWrapper.select("id", "title", "subject", "difficulty", "status", "visibility", "published_at", "author_id");
        listWrapper.orderByDesc("updated_at").orderByDesc("id");
        int offset = Math.max(0, (page - 1) * pageSize);
        listWrapper.last("limit " + offset + ", " + pageSize);

        List<ProblemEntity> entities = problemMapper.selectList(listWrapper);
        Map<Long, List<Long>> tagIdsMap = fetchTagIdsByProblemIds(entities.stream().map(ProblemEntity::getId).toList());
        List<ProblemSummaryData> items = entities.stream()
                .map(entity -> toSummaryData(entity, tagIdsMap.getOrDefault(entity.getId(), List.of())))
                .toList();
        return new PageResponse<>(items, page, pageSize, total);
    }

    @Override
    public List<ProblemSummaryData> listSummariesByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        QueryWrapper<ProblemEntity> wrapper = new QueryWrapper<>();
        wrapper.in("id", ids)
                .eq("deleted", 0);
        wrapper.select("id", "title", "subject", "difficulty", "status", "visibility", "published_at", "author_id");
        List<ProblemEntity> entities = problemMapper.selectList(wrapper);
        Map<Long, List<Long>> tagIdsMap = fetchTagIdsByProblemIds(entities.stream().map(ProblemEntity::getId).toList());
        return entities.stream()
                .map(entity -> toSummaryData(entity, tagIdsMap.getOrDefault(entity.getId(), List.of())))
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProblemDetailData update(ProblemUpdateCommand command) {
        LocalDateTime now = LocalDateTime.now();
        UpdateWrapper<ProblemEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", command.id())
                .set("author_id", command.authorId())
                .set("title", command.title())
                .set("subject", command.subject())
                .set("difficulty", command.difficulty())
                .set("statement_format", command.statementFormat().name())
                .set("statement_content", command.statementContent())
                .set("solution_format", command.solutionFormat() == null ? null : command.solutionFormat().name())
                .set("solution_content", command.solutionContent())
                .set("visibility", command.visibility().name())
                .set("share_key", command.shareKey())
                .set("status", command.status().name())
                .set("published_at", command.publishedAt())
                .set("last_modified_at", now)
                .set("updated_at", now);
        problemMapper.update(null, wrapper);
        replaceTags(command.id(), command.tagIds(), now);
        return findById(command.id()).orElseThrow(() -> new IllegalStateException("题目更新失败"));
    }

    @Override
    public ProblemDetailData publish(long id) {
        ProblemDetailData existing = findById(id)
                .orElseThrow(() -> new IllegalStateException("题目不存在"));
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime publishedAt = existing.publishedAt() == null ? now : existing.publishedAt();
        UpdateWrapper<ProblemEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id)
                .set("status", ProblemStatus.PUBLISHED.name())
                .set("published_at", publishedAt)
                .set("last_modified_at", now)
                .set("updated_at", now);
        problemMapper.update(null, wrapper);
        return findById(id).orElseThrow(() -> new IllegalStateException("题目发布失败"));
    }

    @Override
    public ProblemDetailData disable(long id) {
        LocalDateTime now = LocalDateTime.now();
        UpdateWrapper<ProblemEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id)
                .set("status", ProblemStatus.DISABLED.name())
                .set("updated_at", now);
        problemMapper.update(null, wrapper);
        return findById(id).orElseThrow(() -> new IllegalStateException("题目下架失败"));
    }

    @Override
    public void softDelete(long id) {
        LocalDateTime now = LocalDateTime.now();
        UpdateWrapper<ProblemEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id)
                .set("deleted", 1)
                .set("updated_at", now);
        problemMapper.update(null, wrapper);
    }

    @Override
    public void incrementViewCount(long id) {
        LocalDateTime now = LocalDateTime.now();
        UpdateWrapper<ProblemEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id)
                .eq("deleted", 0)
                .setSql("view_count = view_count + 1")
                .set("updated_at", now);
        problemMapper.update(null, wrapper);
    }

    @Override
    public void incrementFavoriteCount(long id) {
        LocalDateTime now = LocalDateTime.now();
        UpdateWrapper<ProblemEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id)
                .eq("deleted", 0)
                .setSql("favorite_count = favorite_count + 1")
                .set("updated_at", now);
        problemMapper.update(null, wrapper);
    }

    @Override
    public void decrementFavoriteCount(long id) {
        LocalDateTime now = LocalDateTime.now();
        UpdateWrapper<ProblemEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id)
                .eq("deleted", 0)
                .setSql("favorite_count = CASE WHEN favorite_count > 0 THEN favorite_count - 1 ELSE 0 END")
                .set("updated_at", now);
        problemMapper.update(null, wrapper);
    }

    @Override
    public void incrementLikeCount(long id) {
        LocalDateTime now = LocalDateTime.now();
        UpdateWrapper<ProblemEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id)
                .eq("deleted", 0)
                .setSql("like_count = like_count + 1")
                .set("updated_at", now);
        problemMapper.update(null, wrapper);
    }

    @Override
    public void decrementLikeCount(long id) {
        LocalDateTime now = LocalDateTime.now();
        UpdateWrapper<ProblemEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id)
                .eq("deleted", 0)
                .setSql("like_count = CASE WHEN like_count > 0 THEN like_count - 1 ELSE 0 END")
                .set("updated_at", now);
        problemMapper.update(null, wrapper);
    }

    /**
     * 将实体转换为摘要数据。
     *
     * @param entity 题目实体
     * @return 摘要数据
     */
    private static ProblemSummaryData toSummaryData(ProblemEntity entity, List<Long> tagIds) {
        ProblemStatus status = parseProblemStatus(entity.getStatus());
        Visibility visibility = entity.getVisibility() == null ? null : Visibility.valueOf(entity.getVisibility());
        return new ProblemSummaryData(
                entity.getId(),
                entity.getTitle(),
                entity.getSubject(),
                entity.getDifficulty() == null ? 0 : entity.getDifficulty(),
                status,
                visibility,
                entity.getPublishedAt(),
                entity.getAuthorId() == null ? 0L : entity.getAuthorId(),
                tagIds == null ? List.of() : tagIds
        );
    }

    /**
     * 批量获取题目标签ID列表。
     *
     * @param problemIds 题目ID列表
     * @return problemId -> tagIds
     */
    private Map<Long, List<Long>> fetchTagIdsByProblemIds(List<Long> problemIds) {
        if (problemIds == null || problemIds.isEmpty()) {
            return Map.of();
        }
        List<Long> distinctIds = problemIds.stream().filter(Objects::nonNull).distinct().toList();
        if (distinctIds.isEmpty()) {
            return Map.of();
        }
        QueryWrapper<ProblemTagEntity> wrapper = new QueryWrapper<>();
        wrapper.in("problem_id", distinctIds)
                .eq("deleted", 0);
        List<ProblemTagEntity> rows = problemTagMapper.selectList(wrapper);
        return rows.stream().collect(Collectors.groupingBy(
                ProblemTagEntity::getProblemId,
                Collectors.mapping(ProblemTagEntity::getTagId, Collectors.toList())
        ));
    }

    /**
     * 将实体转换为详情数据。
     *
     * @param entity 题目实体
     * @return 详情数据
     */
    private static ProblemDetailData toDetailData(
            ProblemEntity entity,
            List<Long> tagIds
    ) {
        ContentFormat statementFormat = entity.getStatementFormat() == null ? null
                : ContentFormat.valueOf(entity.getStatementFormat());
        ContentFormat solutionFormat = entity.getSolutionFormat() == null ? null
                : ContentFormat.valueOf(entity.getSolutionFormat());
        Visibility visibility = entity.getVisibility() == null ? null : Visibility.valueOf(entity.getVisibility());
        ProblemStatus status = parseProblemStatus(entity.getStatus());
        return new ProblemDetailData(
                entity.getId(),
                entity.getAuthorId() == null ? 0L : entity.getAuthorId(),
                entity.getTitle(),
                entity.getSubject(),
                entity.getDifficulty() == null ? 0 : entity.getDifficulty(),
                statementFormat,
                entity.getStatementContent(),
                solutionFormat,
                entity.getSolutionContent(),
                visibility,
                entity.getShareKey(),
                status,
                entity.getPublishedAt(),
                tagIds
        );
    }

    /**
     * 解析题目状态字符串。
     *
     * @param raw 状态原始值（来自数据库）
     * @return 状态枚举（未知状态返回 DRAFT）
     */
    private static ProblemStatus parseProblemStatus(String raw) {
        if (raw == null) {
            return null;
        }
        // 兼容历史数据：取消审核后，将 PENDING_REVIEW 视为 DRAFT。
        if ("PENDING_REVIEW".equals(raw)) {
            return ProblemStatus.DRAFT;
        }
        try {
            return ProblemStatus.valueOf(raw);
        } catch (IllegalArgumentException ex) {
            return ProblemStatus.DRAFT;
        }
    }

    /**
     * 重置题目的标签关联。
     *
     * @param problemId 题目ID
     * @param tagIds 标签ID列表
     * @param now 当前时间
     */
    private void replaceTags(long problemId, List<Long> tagIds, LocalDateTime now) {
        softDeleteTags(problemId, now);
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }
        for (Long tagId : tagIds) {
            ProblemTagEntity entity = new ProblemTagEntity();
            entity.setProblemId(problemId);
            entity.setTagId(tagId);
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);
            entity.setDeleted(0);
            problemTagMapper.insert(entity);
        }
    }

    /**
     * 软删除题目-标签关联。
     *
     * @param problemId 题目ID
     * @param now 当前时间
     */
    private void softDeleteTags(long problemId, LocalDateTime now) {
        UpdateWrapper<ProblemTagEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("problem_id", problemId)
                .set("deleted", 1)
                .set("updated_at", now);
        problemTagMapper.update(null, wrapper);
    }

    /**
     * 获取题目标签ID列表。
     *
     * @param problemId 题目ID
     * @return 标签ID列表
     */
    private List<Long> fetchTagIds(long problemId) {
        QueryWrapper<ProblemTagEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("problem_id", problemId)
                .eq("deleted", 0);
        return problemTagMapper.selectList(wrapper).stream()
                .map(ProblemTagEntity::getTagId)
                .toList();
    }

    /**
     * 构造公开列表查询条件。
     *
     * @param query 查询参数
     * @return 查询包装器
     */
    private static QueryWrapper<ProblemEntity> buildPublicListWrapper(ProblemQuery query) {
        QueryWrapper<ProblemEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("status", ProblemStatus.PUBLISHED.name())
                .eq("visibility", Visibility.PUBLIC.name())
                .eq("deleted", 0);
        if (query.subject() != null) {
            wrapper.eq("subject", query.subject());
        }
        if (query.tagIds() != null && !query.tagIds().isEmpty()) {
            String inList = query.tagIds().stream().map(String::valueOf).collect(java.util.stream.Collectors.joining(","));
            wrapper.inSql("id", "select distinct problem_id from vf_problem_tag where deleted=0 and tag_id in ("
                    + inList + ")");
        }
        if (query.difficultyMin() != null) {
            wrapper.ge("difficulty", query.difficultyMin());
        }
        if (query.difficultyMax() != null) {
            wrapper.le("difficulty", query.difficultyMax());
        }
        if (query.keyword() != null && !query.keyword().isBlank()) {
            wrapper.like("title", query.keyword().trim());
        }
        return wrapper;
    }
}

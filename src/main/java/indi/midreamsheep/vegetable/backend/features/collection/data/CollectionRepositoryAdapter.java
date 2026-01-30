package indi.midreamsheep.vegetable.backend.features.collection.data;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import indi.midreamsheep.vegetable.backend.common.api.PageResponse;
import indi.midreamsheep.vegetable.backend.features.collection.domain.CollectionStatus;
import indi.midreamsheep.vegetable.backend.features.collection.domain.command.CollectionCreateCommand;
import indi.midreamsheep.vegetable.backend.features.collection.domain.command.CollectionUpdateCommand;
import indi.midreamsheep.vegetable.backend.features.collection.domain.model.CollectionDetailData;
import indi.midreamsheep.vegetable.backend.features.collection.domain.model.CollectionItemData;
import indi.midreamsheep.vegetable.backend.features.collection.domain.model.CollectionSummaryData;
import indi.midreamsheep.vegetable.backend.features.collection.domain.port.CollectionRepositoryPort;
import indi.midreamsheep.vegetable.backend.features.problem.domain.Visibility;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.CollectionEntity;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.CollectionItemEntity;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper.CollectionItemMapper;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper.CollectionMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 题单仓储适配器：通过 MyBatis-Plus 完成持久化。
 */
@Component
public class CollectionRepositoryAdapter implements CollectionRepositoryPort {

    private final CollectionMapper collectionMapper;
    private final CollectionItemMapper collectionItemMapper;

    /**
     * 构造题单仓储适配器。
     *
     * @param collectionMapper 题单 Mapper
     * @param collectionItemMapper 题单条目 Mapper
     */
    public CollectionRepositoryAdapter(
            CollectionMapper collectionMapper,
            CollectionItemMapper collectionItemMapper
    ) {
        this.collectionMapper = collectionMapper;
        this.collectionItemMapper = collectionItemMapper;
    }

    @Override
    public long create(CollectionCreateCommand command) {
        LocalDateTime now = LocalDateTime.now();
        CollectionEntity entity = new CollectionEntity();
        entity.setAuthorId(command.authorId());
        entity.setName(command.name());
        entity.setDescription(command.description());
        entity.setVisibility(command.visibility().name());
        entity.setShareKey(command.shareKey());
        entity.setStatus(CollectionStatus.ACTIVE.name());
        entity.setItemCount(0);
        entity.setDeleted(0);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        collectionMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public Optional<CollectionDetailData> findDetailById(long id) {
        QueryWrapper<CollectionEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id)
                .eq("deleted", 0);
        CollectionEntity entity = collectionMapper.selectOne(wrapper);
        if (entity == null) {
            return Optional.empty();
        }
        List<CollectionItemData> items = listItems(id);
        return Optional.of(toDetailData(entity, items));
    }

    @Override
    public Optional<CollectionDetailData> findDetailByShareKey(String shareKey) {
        QueryWrapper<CollectionEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("share_key", shareKey)
                .eq("deleted", 0);
        CollectionEntity entity = collectionMapper.selectOne(wrapper);
        if (entity == null) {
            return Optional.empty();
        }
        List<CollectionItemData> items = listItems(entity.getId());
        return Optional.of(toDetailData(entity, items));
    }

    @Override
    public PageResponse<CollectionSummaryData> listPublic(int page, int pageSize) {
        QueryWrapper<CollectionEntity> countWrapper = buildPublicListWrapper();
        long total = collectionMapper.selectCount(countWrapper);

        QueryWrapper<CollectionEntity> listWrapper = buildPublicListWrapper();
        listWrapper.orderByDesc("created_at");
        int offset = Math.max(0, (page - 1) * pageSize);
        listWrapper.last("limit " + offset + ", " + pageSize);
        List<CollectionSummaryData> items = collectionMapper.selectList(listWrapper).stream()
                .map(CollectionRepositoryAdapter::toSummaryData)
                .toList();
        return new PageResponse<>(items, page, pageSize, total);
    }

    @Override
    public PageResponse<CollectionSummaryData> listByAuthor(long authorId, CollectionStatus status, int page, int pageSize) {
        QueryWrapper<CollectionEntity> countWrapper = new QueryWrapper<>();
        countWrapper.eq("author_id", authorId).eq("deleted", 0);
        if (status != null) {
            countWrapper.eq("status", status.name());
        }
        long total = collectionMapper.selectCount(countWrapper);

        QueryWrapper<CollectionEntity> listWrapper = new QueryWrapper<>();
        listWrapper.eq("author_id", authorId).eq("deleted", 0);
        if (status != null) {
            listWrapper.eq("status", status.name());
        }
        listWrapper.orderByDesc("updated_at").orderByDesc("id");
        int offset = Math.max(0, (page - 1) * pageSize);
        listWrapper.last("limit " + offset + ", " + pageSize);
        List<CollectionSummaryData> items = collectionMapper.selectList(listWrapper).stream()
                .map(CollectionRepositoryAdapter::toSummaryData)
                .toList();
        return new PageResponse<>(items, page, pageSize, total);
    }

    @Override
    public List<CollectionSummaryData> listSummariesByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        QueryWrapper<CollectionEntity> wrapper = new QueryWrapper<>();
        wrapper.in("id", ids).eq("deleted", 0);
        return collectionMapper.selectList(wrapper).stream()
                .map(CollectionRepositoryAdapter::toSummaryData)
                .toList();
    }

    @Override
    public CollectionDetailData update(CollectionUpdateCommand command) {
        LocalDateTime now = LocalDateTime.now();
        UpdateWrapper<CollectionEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", command.id())
                .set("name", command.name())
                .set("description", command.description())
                .set("visibility", command.visibility().name())
                .set("share_key", command.shareKey())
                .set("updated_at", now);
        collectionMapper.update(null, wrapper);
        return findDetailById(command.id()).orElseThrow(() -> new IllegalStateException("题单更新失败"));
    }

    @Override
    public void softDelete(long id) {
        LocalDateTime now = LocalDateTime.now();
        UpdateWrapper<CollectionEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id)
                .set("deleted", 1)
                .set("updated_at", now);
        collectionMapper.update(null, wrapper);

        UpdateWrapper<CollectionItemEntity> itemWrapper = new UpdateWrapper<>();
        itemWrapper.eq("collection_id", id)
                .eq("deleted", 0)
                .set("deleted", 1)
                .set("updated_at", now);
        collectionItemMapper.update(null, itemWrapper);
    }

    @Override
    public boolean existsItem(long collectionId, long problemId) {
        QueryWrapper<CollectionItemEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("collection_id", collectionId)
                .eq("problem_id", problemId)
                .eq("deleted", 0);
        return collectionItemMapper.selectCount(wrapper) > 0;
    }

    @Override
    public void addItem(long collectionId, long problemId, int sortOrder) {
        LocalDateTime now = LocalDateTime.now();
        CollectionItemEntity entity = new CollectionItemEntity();
        entity.setCollectionId(collectionId);
        entity.setProblemId(problemId);
        entity.setSortOrder(sortOrder);
        entity.setAddedAt(now);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setDeleted(0);
        collectionItemMapper.insert(entity);

        UpdateWrapper<CollectionEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", collectionId)
                .setSql("item_count = item_count + 1")
                .set("updated_at", now);
        collectionMapper.update(null, updateWrapper);
    }

    @Override
    public void updateItemOrders(long collectionId, List<CollectionItemData> items) {
        LocalDateTime now = LocalDateTime.now();
        for (CollectionItemData item : items) {
            UpdateWrapper<CollectionItemEntity> wrapper = new UpdateWrapper<>();
            wrapper.eq("collection_id", collectionId)
                    .eq("problem_id", item.problemId())
                    .set("sort_order", item.sortOrder())
                    .set("updated_at", now);
            collectionItemMapper.update(null, wrapper);
        }
        UpdateWrapper<CollectionEntity> collectionWrapper = new UpdateWrapper<>();
        collectionWrapper.eq("id", collectionId)
                .set("updated_at", now);
        collectionMapper.update(null, collectionWrapper);
    }

    @Override
    public boolean removeItem(long collectionId, long problemId) {
        LocalDateTime now = LocalDateTime.now();
        UpdateWrapper<CollectionItemEntity> itemWrapper = new UpdateWrapper<>();
        itemWrapper.eq("collection_id", collectionId)
                .eq("problem_id", problemId)
                .eq("deleted", 0)
                .set("deleted", 1)
                .set("updated_at", now);
        int affected = collectionItemMapper.update(null, itemWrapper);
        if (affected <= 0) {
            return false;
        }

        UpdateWrapper<CollectionEntity> collectionWrapper = new UpdateWrapper<>();
        collectionWrapper.eq("id", collectionId)
                .setSql("item_count = CASE WHEN item_count > 0 THEN item_count - 1 ELSE 0 END")
                .set("updated_at", now);
        collectionMapper.update(null, collectionWrapper);
        return true;
    }

    /**
     * 查询题单条目。
     *
     * @param collectionId 题单ID
     * @return 条目列表
     */
    private List<CollectionItemData> listItems(long collectionId) {
        QueryWrapper<CollectionItemEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("collection_id", collectionId)
                .eq("deleted", 0)
                .orderByAsc("sort_order")
                .orderByAsc("id");
        return collectionItemMapper.selectList(wrapper).stream()
                .map(CollectionRepositoryAdapter::toItemData)
                .toList();
    }

    /**
     * 将条目实体转换为领域模型。
     *
     * @param entity 条目实体
     * @return 条目数据
     */
    private static CollectionItemData toItemData(CollectionItemEntity entity) {
        return new CollectionItemData(
                entity.getProblemId() == null ? 0L : entity.getProblemId(),
                entity.getSortOrder() == null ? 0 : entity.getSortOrder()
        );
    }

    /**
     * 将题单实体转换为详情数据。
     *
     * @param entity 题单实体
     * @param items 条目列表
     * @return 题单详情
     */
    private static CollectionDetailData toDetailData(CollectionEntity entity, List<CollectionItemData> items) {
        Visibility visibility = entity.getVisibility() == null ? null : Visibility.valueOf(entity.getVisibility());
        CollectionStatus status = entity.getStatus() == null ? null : CollectionStatus.valueOf(entity.getStatus());
        return new CollectionDetailData(
                entity.getId(),
                entity.getAuthorId() == null ? 0L : entity.getAuthorId(),
                entity.getName(),
                entity.getDescription(),
                visibility,
                entity.getShareKey(),
                status,
                items
        );
    }

    /**
     * 构造公开列表查询条件。
     *
     * @return 查询包装器
     */
    private static QueryWrapper<CollectionEntity> buildPublicListWrapper() {
        QueryWrapper<CollectionEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("status", CollectionStatus.ACTIVE.name())
                .eq("visibility", Visibility.PUBLIC.name())
                .eq("deleted", 0);
        return wrapper;
    }

    /**
     * 将题单实体转换为摘要数据。
     *
     * @param entity 题单实体
     * @return 摘要数据
     */
    private static CollectionSummaryData toSummaryData(CollectionEntity entity) {
        Visibility visibility = entity.getVisibility() == null ? null : Visibility.valueOf(entity.getVisibility());
        CollectionStatus status = entity.getStatus() == null ? null : CollectionStatus.valueOf(entity.getStatus());
        return new CollectionSummaryData(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                visibility,
                status,
                entity.getItemCount() == null ? 0 : entity.getItemCount(),
                entity.getAuthorId() == null ? 0L : entity.getAuthorId()
        );
    }
}

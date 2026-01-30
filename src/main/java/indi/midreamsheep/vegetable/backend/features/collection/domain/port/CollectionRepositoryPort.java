package indi.midreamsheep.vegetable.backend.features.collection.domain.port;

import indi.midreamsheep.vegetable.backend.common.api.PageResponse;
import indi.midreamsheep.vegetable.backend.features.collection.domain.CollectionStatus;
import indi.midreamsheep.vegetable.backend.features.collection.domain.command.CollectionCreateCommand;
import indi.midreamsheep.vegetable.backend.features.collection.domain.command.CollectionUpdateCommand;
import indi.midreamsheep.vegetable.backend.features.collection.domain.model.CollectionDetailData;
import indi.midreamsheep.vegetable.backend.features.collection.domain.model.CollectionItemData;
import indi.midreamsheep.vegetable.backend.features.collection.domain.model.CollectionSummaryData;

import java.util.List;
import java.util.Optional;

/**
 * 题单仓储端口。
 */
public interface CollectionRepositoryPort {

    /**
     * 创建题单。
     *
     * @param command 创建命令
     * @return 题单ID
     */
    long create(CollectionCreateCommand command);

    /**
     * 获取题单详情（含条目）。
     *
     * @param id 题单ID
     * @return 题单详情
     */
    Optional<CollectionDetailData> findDetailById(long id);

    /**
     * 通过 shareKey 获取题单详情。
     *
     * @param shareKey 分享 key
     * @return 题单详情
     */
    Optional<CollectionDetailData> findDetailByShareKey(String shareKey);

    /**
     * 查询公开题单列表（分页）。
     *
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResponse<CollectionSummaryData> listPublic(int page, int pageSize);

    /**
     * 查询作者的题单列表（分页）。
     *
     * @param authorId 作者ID
     * @param status 状态筛选（可为空）
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResponse<CollectionSummaryData> listByAuthor(long authorId, CollectionStatus status, int page, int pageSize);

    /**
     * 根据一组 ID 批量查询题单摘要（不保证顺序）。
     *
     * @param ids 题单ID列表
     * @return 摘要列表
     */
    List<CollectionSummaryData> listSummariesByIds(List<Long> ids);

    /**
     * 更新题单信息，并返回最新详情。
     *
     * @param command 更新命令
     * @return 更新后的题单详情
     */
    CollectionDetailData update(CollectionUpdateCommand command);

    /**
     * 软删除题单。
     *
     * @param id 题单ID
     */
    void softDelete(long id);

    /**
     * 判断题单条目是否存在。
     *
     * @param collectionId 题单ID
     * @param problemId 题目ID
     * @return 是否存在
     */
    boolean existsItem(long collectionId, long problemId);

    /**
     * 添加题单条目。
     *
     * @param collectionId 题单ID
     * @param problemId 题目ID
     * @param sortOrder 排序
     */
    void addItem(long collectionId, long problemId, int sortOrder);

    /**
     * 更新题单条目排序。
     *
     * @param collectionId 题单ID
     * @param items 排序项
     */
    void updateItemOrders(long collectionId, List<CollectionItemData> items);

    /**
     * 从题单移除题目（软删除条目，同时维护 item_count）。
     *
     * @param collectionId 题单ID
     * @param problemId 题目ID
     * @return 是否移除成功（原本存在且未删除）
     */
    boolean removeItem(long collectionId, long problemId);
}

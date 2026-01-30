package indi.midreamsheep.vegetable.backend.features.problem.domain.port;

import indi.midreamsheep.vegetable.backend.common.api.PageResponse;
import indi.midreamsheep.vegetable.backend.features.problem.domain.ProblemStatus;
import indi.midreamsheep.vegetable.backend.features.problem.domain.command.ProblemCreateCommand;
import indi.midreamsheep.vegetable.backend.features.problem.domain.command.ProblemUpdateCommand;
import indi.midreamsheep.vegetable.backend.features.problem.domain.model.ProblemDetailData;
import indi.midreamsheep.vegetable.backend.features.problem.domain.model.ProblemSummaryData;
import indi.midreamsheep.vegetable.backend.features.problem.domain.query.ProblemQuery;

import java.util.List;
import java.util.Optional;

/**
 * 题目仓储端口：定义领域对持久化的依赖。
 */
public interface ProblemRepositoryPort {

    /**
     * 创建题目并返回题目ID。
     *
     * @param command 创建命令
     * @return 题目ID
     */
    long create(ProblemCreateCommand command);

    /**
     * 根据 ID 获取题目详情。
     *
     * @param id 题目ID
     * @return 题目详情（可为空）
     */
    Optional<ProblemDetailData> findById(long id);

    /**
     * 根据 shareKey 获取题目详情。
     *
     * @param shareKey 分享 key
     * @return 题目详情（可为空）
     */
    Optional<ProblemDetailData> findByShareKey(String shareKey);

    /**
     * 查询公开题目列表（分页）。
     *
     * @param query 查询条件
     * @return 分页结果
     */
    PageResponse<ProblemSummaryData> listPublic(ProblemQuery query);

    /**
     * 查询作者的题目列表（分页）。
     *
     * @param authorId 作者ID
     * @param status 状态（可为空）
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResponse<ProblemSummaryData> listByAuthor(long authorId, ProblemStatus status, int page, int pageSize);

    /**
     * 根据一组 ID 批量查询题目摘要（不保证顺序）。
     *
     * @param ids 题目ID列表
     * @return 摘要列表
     */
    List<ProblemSummaryData> listSummariesByIds(List<Long> ids);

    /**
     * 更新题目信息，并返回最新详情。
     *
     * @param command 更新命令
     * @return 更新后的题目详情
     */
    ProblemDetailData update(ProblemUpdateCommand command);

    /**
     * 发布题目，并返回最新详情。
     *
     * @param id 题目ID
     * @return 发布后的题目详情
     */
    ProblemDetailData publish(long id);

    /**
     * 下架题目。
     *
     * @param id 题目ID
     * @return 更新后的题目详情
     */
    ProblemDetailData disable(long id);

    /**
     * 软删除题目。
     *
     * @param id 题目ID
     */
    void softDelete(long id);

    /**
     * 题目浏览量 +1。
     *
     * @param id 题目ID
     */
    void incrementViewCount(long id);

    /**
     * 题目收藏数 +1。
     *
     * @param id 题目ID
     */
    void incrementFavoriteCount(long id);

    /**
     * 题目收藏数 -1（不会减到负数）。
     *
     * @param id 题目ID
     */
    void decrementFavoriteCount(long id);

    /**
     * 题目点赞数 +1。
     *
     * @param id 题目ID
     */
    void incrementLikeCount(long id);

    /**
     * 题目点赞数 -1（不会减到负数）。
     *
     * @param id 题目ID
     */
    void decrementLikeCount(long id);

}

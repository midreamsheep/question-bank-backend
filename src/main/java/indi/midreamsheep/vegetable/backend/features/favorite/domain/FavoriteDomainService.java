package indi.midreamsheep.vegetable.backend.features.favorite.domain;

import indi.midreamsheep.vegetable.backend.common.api.PageResponse;
import indi.midreamsheep.vegetable.backend.common.error.BizException;
import indi.midreamsheep.vegetable.backend.common.error.ErrorCode;
import indi.midreamsheep.vegetable.backend.features.collection.domain.CollectionStatus;
import indi.midreamsheep.vegetable.backend.features.collection.domain.model.CollectionSummaryData;
import indi.midreamsheep.vegetable.backend.features.collection.domain.port.CollectionRepositoryPort;
import indi.midreamsheep.vegetable.backend.features.favorite.domain.port.FavoriteCollectionRepositoryPort;
import indi.midreamsheep.vegetable.backend.features.favorite.domain.port.FavoriteProblemRepositoryPort;
import indi.midreamsheep.vegetable.backend.features.problem.domain.ProblemStatus;
import indi.midreamsheep.vegetable.backend.features.problem.domain.Visibility;
import indi.midreamsheep.vegetable.backend.features.problem.domain.model.ProblemSummaryData;
import indi.midreamsheep.vegetable.backend.features.problem.domain.port.ProblemRepositoryPort;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 收藏领域服务：封装题目/题单收藏与列表逻辑。
 */
public class FavoriteDomainService {

    private final FavoriteProblemRepositoryPort favoriteProblemRepositoryPort;
    private final FavoriteCollectionRepositoryPort favoriteCollectionRepositoryPort;
    private final ProblemRepositoryPort problemRepositoryPort;
    private final CollectionRepositoryPort collectionRepositoryPort;

    /**
     * 构造收藏领域服务。
     *
     * @param favoriteProblemRepositoryPort 收藏题目仓储端口
     * @param favoriteCollectionRepositoryPort 收藏题单仓储端口
     * @param problemRepositoryPort 题目仓储端口
     * @param collectionRepositoryPort 题单仓储端口
     */
    public FavoriteDomainService(
            FavoriteProblemRepositoryPort favoriteProblemRepositoryPort,
            FavoriteCollectionRepositoryPort favoriteCollectionRepositoryPort,
            ProblemRepositoryPort problemRepositoryPort,
            CollectionRepositoryPort collectionRepositoryPort
    ) {
        this.favoriteProblemRepositoryPort = favoriteProblemRepositoryPort;
        this.favoriteCollectionRepositoryPort = favoriteCollectionRepositoryPort;
        this.problemRepositoryPort = problemRepositoryPort;
        this.collectionRepositoryPort = collectionRepositoryPort;
    }

    /**
     * 收藏题目（幂等）。
     *
     * @param userId 用户ID
     * @param problemId 题目ID
     */
    public void favoriteProblem(long userId, long problemId) {
        requireValidIds(userId, problemId);
        ProblemSummaryData summary = requireProblemSummary(problemId);
        if (!canAccessProblemById(summary, userId)) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权限收藏该题目");
        }
        boolean changed = favoriteProblemRepositoryPort.add(userId, problemId);
        if (changed) {
            problemRepositoryPort.incrementFavoriteCount(problemId);
        }
    }

    /**
     * 取消收藏题目（幂等）。
     *
     * @param userId 用户ID
     * @param problemId 题目ID
     */
    public void unfavoriteProblem(long userId, long problemId) {
        requireValidIds(userId, problemId);
        // 取消收藏允许目标不存在/不可访问（幂等），避免客户端状态不同步导致 4xx。
        boolean changed = favoriteProblemRepositoryPort.remove(userId, problemId);
        if (changed) {
            problemRepositoryPort.decrementFavoriteCount(problemId);
        }
    }

    /**
     * 我收藏的题目列表（分页）。
     *
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public PageResponse<ProblemSummaryData> listFavoriteProblems(long userId, int page, int pageSize) {
        if (userId <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "userId 不合法");
        }
        PageResponse<Long> idPage = favoriteProblemRepositoryPort.listProblemIds(userId, page, pageSize);
        List<Long> ids = idPage.items();
        if (ids.isEmpty()) {
            return new PageResponse<>(List.of(), idPage.page(), idPage.pageSize(), idPage.total());
        }

        List<ProblemSummaryData> summaries = problemRepositoryPort.listSummariesByIds(ids);
        Map<Long, ProblemSummaryData> map = new HashMap<>();
        for (ProblemSummaryData s : summaries) {
            map.put(s.id(), s);
        }
        List<ProblemSummaryData> ordered = ids.stream()
                .map(map::get)
                .filter(s -> s != null && canAccessProblemById(s, userId))
                .toList();
        return new PageResponse<>(ordered, idPage.page(), idPage.pageSize(), idPage.total());
    }

    /**
     * 收藏题单（幂等）。
     *
     * @param userId 用户ID
     * @param collectionId 题单ID
     */
    public void favoriteCollection(long userId, long collectionId) {
        requireValidIds(userId, collectionId);
        CollectionSummaryData summary = requireCollectionSummary(collectionId);
        if (!canAccessCollectionById(summary, userId)) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权限收藏该题单");
        }
        favoriteCollectionRepositoryPort.add(userId, collectionId);
    }

    /**
     * 取消收藏题单（幂等）。
     *
     * @param userId 用户ID
     * @param collectionId 题单ID
     */
    public void unfavoriteCollection(long userId, long collectionId) {
        requireValidIds(userId, collectionId);
        favoriteCollectionRepositoryPort.remove(userId, collectionId);
    }

    /**
     * 我收藏的题单列表（分页）。
     *
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public PageResponse<CollectionSummaryData> listFavoriteCollections(long userId, int page, int pageSize) {
        if (userId <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "userId 不合法");
        }
        PageResponse<Long> idPage = favoriteCollectionRepositoryPort.listCollectionIds(userId, page, pageSize);
        List<Long> ids = idPage.items();
        if (ids.isEmpty()) {
            return new PageResponse<>(List.of(), idPage.page(), idPage.pageSize(), idPage.total());
        }

        List<CollectionSummaryData> summaries = collectionRepositoryPort.listSummariesByIds(ids);
        Map<Long, CollectionSummaryData> map = new HashMap<>();
        for (CollectionSummaryData s : summaries) {
            map.put(s.id(), s);
        }
        List<CollectionSummaryData> ordered = ids.stream()
                .map(map::get)
                .filter(s -> s != null && canAccessCollectionById(s, userId))
                .toList();
        return new PageResponse<>(ordered, idPage.page(), idPage.pageSize(), idPage.total());
    }

    /**
     * 获取题目摘要（不存在则抛 404）。
     *
     * @param problemId 题目ID
     * @return 题目摘要
     */
    private ProblemSummaryData requireProblemSummary(long problemId) {
        List<ProblemSummaryData> list = problemRepositoryPort.listSummariesByIds(List.of(problemId));
        if (list.isEmpty()) {
            throw new BizException(ErrorCode.NOT_FOUND, "题目不存在");
        }
        return list.get(0);
    }

    /**
     * 获取题单摘要（不存在则抛 404）。
     *
     * @param collectionId 题单ID
     * @return 题单摘要
     */
    private CollectionSummaryData requireCollectionSummary(long collectionId) {
        List<CollectionSummaryData> list = collectionRepositoryPort.listSummariesByIds(List.of(collectionId));
        if (list.isEmpty()) {
            throw new BizException(ErrorCode.NOT_FOUND, "题单不存在");
        }
        return list.get(0);
    }

    /**
     * 校验用户是否可通过 ID 访问题目（用于收藏/收藏列表展示）。
     *
     * @param s 题目摘要
     * @param userId 用户ID
     * @return 是否可访问
     */
    private static boolean canAccessProblemById(ProblemSummaryData s, long userId) {
        if (s.authorId() == userId) {
            return true;
        }
        return s.status() == ProblemStatus.PUBLISHED && s.visibility() == Visibility.PUBLIC;
    }

    /**
     * 校验用户是否可通过 ID 访问题单（用于收藏/收藏列表展示）。
     *
     * @param s 题单摘要
     * @param userId 用户ID
     * @return 是否可访问
     */
    private static boolean canAccessCollectionById(CollectionSummaryData s, long userId) {
        if (s.authorId() == userId) {
            return true;
        }
        return s.status() == CollectionStatus.ACTIVE && s.visibility() == Visibility.PUBLIC;
    }

    /**
     * 校验基础参数合法性。
     *
     * @param userId 用户ID
     * @param targetId 目标ID（题目/题单）
     */
    private static void requireValidIds(long userId, long targetId) {
        if (userId <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "userId 不合法");
        }
        if (targetId <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "id 不合法");
        }
    }
}

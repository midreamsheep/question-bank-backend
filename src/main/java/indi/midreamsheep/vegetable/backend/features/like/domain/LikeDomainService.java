package indi.midreamsheep.vegetable.backend.features.like.domain;

import indi.midreamsheep.vegetable.backend.common.api.PageResponse;
import indi.midreamsheep.vegetable.backend.common.error.BizException;
import indi.midreamsheep.vegetable.backend.common.error.ErrorCode;
import indi.midreamsheep.vegetable.backend.features.like.domain.port.LikeProblemRepositoryPort;
import indi.midreamsheep.vegetable.backend.features.problem.domain.ProblemStatus;
import indi.midreamsheep.vegetable.backend.features.problem.domain.Visibility;
import indi.midreamsheep.vegetable.backend.features.problem.domain.model.ProblemSummaryData;
import indi.midreamsheep.vegetable.backend.features.problem.domain.port.ProblemRepositoryPort;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 点赞领域服务：封装题目点赞与列表逻辑。
 */
public class LikeDomainService {

    private final LikeProblemRepositoryPort likeProblemRepositoryPort;
    private final ProblemRepositoryPort problemRepositoryPort;

    /**
     * 构造点赞领域服务。
     *
     * @param likeProblemRepositoryPort 点赞题目仓储端口
     * @param problemRepositoryPort 题目仓储端口
     */
    public LikeDomainService(
            LikeProblemRepositoryPort likeProblemRepositoryPort,
            ProblemRepositoryPort problemRepositoryPort
    ) {
        this.likeProblemRepositoryPort = likeProblemRepositoryPort;
        this.problemRepositoryPort = problemRepositoryPort;
    }

    /**
     * 点赞题目（幂等）。
     *
     * @param userId 用户ID
     * @param problemId 题目ID
     */
    public void likeProblem(long userId, long problemId) {
        requireValidIds(userId, problemId);
        ProblemSummaryData summary = requireProblemSummary(problemId);
        if (!canAccessProblemById(summary, userId)) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权限点赞该题目");
        }
        boolean changed = likeProblemRepositoryPort.add(userId, problemId);
        if (changed) {
            problemRepositoryPort.incrementLikeCount(problemId);
        }
    }

    /**
     * 取消点赞（幂等）。
     *
     * @param userId 用户ID
     * @param problemId 题目ID
     */
    public void unlikeProblem(long userId, long problemId) {
        requireValidIds(userId, problemId);
        boolean changed = likeProblemRepositoryPort.remove(userId, problemId);
        if (changed) {
            problemRepositoryPort.decrementLikeCount(problemId);
        }
    }

    /**
     * 我点赞的题目列表（分页）。
     *
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public PageResponse<ProblemSummaryData> listLikedProblems(long userId, int page, int pageSize) {
        if (userId <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "userId 不合法");
        }
        PageResponse<Long> idPage = likeProblemRepositoryPort.listProblemIds(userId, page, pageSize);
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
     * 校验用户是否可通过 ID 访问题目（用于点赞/点赞列表展示）。
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
     * 校验基础参数合法性。
     *
     * @param userId 用户ID
     * @param targetId 目标ID（题目）
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


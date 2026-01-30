package indi.midreamsheep.vegetable.backend.features.like.domain;

import indi.midreamsheep.vegetable.backend.common.api.PageResponse;
import indi.midreamsheep.vegetable.backend.common.error.BizException;
import indi.midreamsheep.vegetable.backend.common.error.ErrorCode;
import indi.midreamsheep.vegetable.backend.features.comment.domain.model.ProblemCommentData;
import indi.midreamsheep.vegetable.backend.features.comment.domain.port.ProblemCommentRepositoryPort;
import indi.midreamsheep.vegetable.backend.features.like.domain.port.LikeCommentRepositoryPort;
import indi.midreamsheep.vegetable.backend.features.problem.domain.ProblemStatus;
import indi.midreamsheep.vegetable.backend.features.problem.domain.Visibility;
import indi.midreamsheep.vegetable.backend.features.problem.domain.model.ProblemDetailData;
import indi.midreamsheep.vegetable.backend.features.problem.domain.port.ProblemRepositoryPort;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评论点赞领域服务。
 */
public class LikeCommentDomainService {

    private final LikeCommentRepositoryPort likeCommentRepositoryPort;
    private final ProblemCommentRepositoryPort problemCommentRepositoryPort;
    private final ProblemRepositoryPort problemRepositoryPort;

    /**
     * 构造评论点赞领域服务。
     *
     * @param likeCommentRepositoryPort 评论点赞仓储端口
     * @param problemCommentRepositoryPort 评论仓储端口
     * @param problemRepositoryPort 题目仓储端口
     */
    public LikeCommentDomainService(
            LikeCommentRepositoryPort likeCommentRepositoryPort,
            ProblemCommentRepositoryPort problemCommentRepositoryPort,
            ProblemRepositoryPort problemRepositoryPort
    ) {
        this.likeCommentRepositoryPort = likeCommentRepositoryPort;
        this.problemCommentRepositoryPort = problemCommentRepositoryPort;
        this.problemRepositoryPort = problemRepositoryPort;
    }

    /**
     * 点赞评论（幂等）。
     *
     * @param problemId 题目ID（路径参数）
     * @param commentId 评论ID
     * @param userId 用户ID
     */
    public void like(long problemId, long commentId, long userId) {
        requireIds(problemId, commentId, userId);
        ProblemCommentData comment = problemCommentRepositoryPort.findById(commentId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "评论不存在"));
        if (comment.problemId() != problemId) {
            throw new BizException(ErrorCode.NOT_FOUND, "评论不存在");
        }
        ProblemDetailData problem = problemRepositoryPort.findById(problemId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "题目不存在"));
        if (!canAccessProblem(problem, userId)) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权限点赞该评论");
        }
        boolean changed = likeCommentRepositoryPort.add(userId, commentId);
        if (changed) {
            problemCommentRepositoryPort.incrementLikeCount(commentId);
        }
    }

    /**
     * 取消点赞评论（幂等）。
     *
     * @param problemId 题目ID（路径参数）
     * @param commentId 评论ID
     * @param userId 用户ID
     */
    public void unlike(long problemId, long commentId, long userId) {
        requireIds(problemId, commentId, userId);
        // 若路径中的 problemId 与 commentId 不匹配，则按不存在处理，避免跨资源误操作。
        problemCommentRepositoryPort.findByIdIncludingDeleted(commentId).ifPresent(comment -> {
            if (comment.problemId() != problemId) {
                throw new BizException(ErrorCode.NOT_FOUND, "评论不存在");
            }
        });

        // 取消点赞允许目标不存在/不可访问（幂等），避免客户端状态不同步导致 4xx。
        boolean changed = likeCommentRepositoryPort.remove(userId, commentId);
        if (changed) {
            problemCommentRepositoryPort.decrementLikeCount(commentId);
        }
    }

    /**
     * 我点赞的评论列表（分页）。
     *
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public PageResponse<ProblemCommentData> listLikedComments(long userId, int page, int pageSize) {
        if (userId <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "userId 不合法");
        }
        PageResponse<Long> idPage = likeCommentRepositoryPort.listCommentIds(userId, page, pageSize);
        List<Long> ids = idPage.items();
        if (ids.isEmpty()) {
            return new PageResponse<>(List.of(), idPage.page(), idPage.pageSize(), idPage.total());
        }
        List<ProblemCommentData> comments = listCommentsByIds(ids);
        Map<Long, ProblemCommentData> map = new HashMap<>();
        for (ProblemCommentData c : comments) {
            map.put(c.id(), c);
        }
        List<ProblemCommentData> ordered = ids.stream().map(map::get).filter(c -> c != null).toList();
        return new PageResponse<>(ordered, idPage.page(), idPage.pageSize(), idPage.total());
    }

    /**
     * 批量获取评论（仅未删除）。
     *
     * @param ids 评论ID列表
     * @return 评论列表
     */
    private List<ProblemCommentData> listCommentsByIds(List<Long> ids) {
        return ids.stream()
                .map(problemCommentRepositoryPort::findById)
                .flatMap(java.util.Optional::stream)
                .toList();
    }

    /**
     * 题目访问权限：公开已发布或作者本人。
     *
     * @param problem 题目
     * @param userId 用户ID
     * @return 是否可访问
     */
    private static boolean canAccessProblem(ProblemDetailData problem, long userId) {
        if (userId == problem.authorId()) {
            return true;
        }
        return problem.visibility() == Visibility.PUBLIC && problem.status() == ProblemStatus.PUBLISHED;
    }

    /**
     * 校验基础参数合法性。
     *
     * @param problemId 题目ID
     * @param commentId 评论ID
     * @param userId 用户ID
     */
    private static void requireIds(long problemId, long commentId, long userId) {
        if (problemId <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "problemId 不合法");
        }
        if (commentId <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "commentId 不合法");
        }
        if (userId <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "userId 不合法");
        }
    }
}

package indi.midreamsheep.vegetable.backend.features.comment.domain;

import indi.midreamsheep.vegetable.backend.common.api.PageResponse;
import indi.midreamsheep.vegetable.backend.common.error.BizException;
import indi.midreamsheep.vegetable.backend.common.error.ErrorCode;
import indi.midreamsheep.vegetable.backend.features.comment.domain.model.ProblemCommentData;
import indi.midreamsheep.vegetable.backend.features.comment.domain.port.ProblemCommentRepositoryPort;
import indi.midreamsheep.vegetable.backend.features.problem.domain.ProblemStatus;
import indi.midreamsheep.vegetable.backend.features.problem.domain.Visibility;
import indi.midreamsheep.vegetable.backend.features.problem.domain.model.ProblemDetailData;
import indi.midreamsheep.vegetable.backend.features.problem.domain.port.ProblemRepositoryPort;
import org.springframework.util.StringUtils;

/**
 * 评论领域服务：围绕题目评论的创建/查询/删除。
 */
public class ProblemCommentDomainService {

    private final ProblemCommentRepositoryPort problemCommentRepositoryPort;
    private final ProblemRepositoryPort problemRepositoryPort;

    /**
     * 构造评论领域服务。
     *
     * @param problemCommentRepositoryPort 评论仓储端口
     * @param problemRepositoryPort 题目仓储端口
     */
    public ProblemCommentDomainService(
            ProblemCommentRepositoryPort problemCommentRepositoryPort,
            ProblemRepositoryPort problemRepositoryPort
    ) {
        this.problemCommentRepositoryPort = problemCommentRepositoryPort;
        this.problemRepositoryPort = problemRepositoryPort;
    }

    /**
     * 创建评论。
     *
     * @param problemId 题目ID
     * @param userId 评论用户ID
     * @param parentId 父评论ID（可为空）
     * @param replyToCommentId 回复的评论ID（可为空）
     * @param content 评论内容
     * @return 评论ID
     */
    public long create(long problemId, long userId, Long parentId, Long replyToCommentId, String content) {
        if (problemId <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "problemId 不合法");
        }
        if (userId <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "userId 不合法");
        }
        if (!StringUtils.hasText(content)) {
            throw new BizException(ErrorCode.BAD_REQUEST, "content 不能为空");
        }
        String finalContent = content.trim();
        if (finalContent.length() > 2000) {
            throw new BizException(ErrorCode.BAD_REQUEST, "content 过长");
        }
        ProblemDetailData problem = problemRepositoryPort.findById(problemId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "题目不存在"));
        if (!canAccessProblem(problem, userId)) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权限评论该题目");
        }
        if (problem.status() != ProblemStatus.PUBLISHED) {
            throw new BizException(ErrorCode.BAD_REQUEST, "仅可评论已发布题目");
        }
        ParentInfo parent = resolveParent(problemId, parentId, replyToCommentId);
        return problemCommentRepositoryPort.create(
                problemId,
                userId,
                parent.parentId(),
                parent.replyToCommentId(),
                finalContent
        );
    }

    /**
     * 评论列表（分页）。
     *
     * @param problemId 题目ID
     * @param requesterId 访问者ID（可为空）
     * @param parentId 父评论ID（可为空，表示顶层）
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public PageResponse<ProblemCommentData> list(long problemId, Long requesterId, Long parentId, int page, int pageSize) {
        if (problemId <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "problemId 不合法");
        }
        if (parentId != null && parentId <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "parentId 不合法");
        }
        if (page < 1) {
            throw new BizException(ErrorCode.BAD_REQUEST, "page 必须从 1 开始");
        }
        if (pageSize < 1 || pageSize > 100) {
            throw new BizException(ErrorCode.BAD_REQUEST, "pageSize 必须在 1-100 范围内");
        }
        ProblemDetailData problem = problemRepositoryPort.findById(problemId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "题目不存在"));
        long uid = requesterId == null ? -1L : requesterId;
        if (!canAccessProblem(problem, uid)) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权限查看评论");
        }
        if (problem.visibility() == Visibility.PUBLIC && problem.status() == ProblemStatus.PUBLISHED) {
            // ok
        } else if (uid > 0 && uid == problem.authorId()) {
            // ok
        } else {
            throw new BizException(ErrorCode.FORBIDDEN, "无权限查看评论");
        }
        return problemCommentRepositoryPort.listByProblem(problemId, parentId, page, pageSize);
    }

    /**
     * 获取评论详情。
     *
     * @param commentId 评论ID
     * @return 评论数据
     */
    public ProblemCommentData get(long commentId) {
        if (commentId <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "commentId 不合法");
        }
        return problemCommentRepositoryPort.findById(commentId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "评论不存在"));
    }

    /**
     * 获取评论（包含已删除）。
     *
     * @param commentId 评论ID
     * @return 评论数据
     */
    public ProblemCommentData getIncludingDeleted(long commentId) {
        if (commentId <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "commentId 不合法");
        }
        return problemCommentRepositoryPort.findByIdIncludingDeleted(commentId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "评论不存在"));
    }

    /**
     * 删除评论（软删除）。
     *
     * @param commentId 评论ID
     */
    public void delete(long commentId) {
        if (commentId <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "commentId 不合法");
        }
        problemCommentRepositoryPort.softDelete(commentId);
    }

    /**
     * 题目访问权限：公开已发布或作者本人。
     *
     * @param problem 题目
     * @param userId 用户ID（未登录用 -1）
     * @return 是否可访问
     */
    private static boolean canAccessProblem(ProblemDetailData problem, long userId) {
        if (userId > 0 && userId == problem.authorId()) {
            return true;
        }
        return problem.visibility() == Visibility.PUBLIC && problem.status() == ProblemStatus.PUBLISHED;
    }

    /**
     * 解析楼中楼父子关系。
     *
     * @param problemId 题目ID
     * @param parentId 父评论ID（可为空）
     * @param replyToCommentId 回复的评论ID（可为空）
     * @return 父子关系信息
     */
    private ParentInfo resolveParent(long problemId, Long parentId, Long replyToCommentId) {
        Long finalParentId = parentId;
        Long finalReplyToId = replyToCommentId;

        if (finalParentId == null && finalReplyToId != null) {
            finalParentId = finalReplyToId;
        }
        if (finalParentId == null) {
            return new ParentInfo(null, null);
        }
        if (finalParentId <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "parentId 不合法");
        }
        if (finalReplyToId != null && finalReplyToId <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "replyToCommentId 不合法");
        }
        ProblemCommentData parent = getIncludingDeleted(finalParentId);
        if (parent.problemId() != problemId) {
            throw new BizException(ErrorCode.BAD_REQUEST, "parentId 不属于该题目");
        }
        if (finalReplyToId != null) {
            ProblemCommentData replyTo = getIncludingDeleted(finalReplyToId);
            if (replyTo.problemId() != problemId) {
                throw new BizException(ErrorCode.BAD_REQUEST, "replyToCommentId 不属于该题目");
            }
        } else {
            finalReplyToId = finalParentId;
        }
        return new ParentInfo(finalParentId, finalReplyToId);
    }

    /**
     * 父子关系信息。
     *
     * @param parentId 父评论ID
     * @param replyToCommentId 回复的评论ID
     */
    private record ParentInfo(Long parentId, Long replyToCommentId) {
    }
}

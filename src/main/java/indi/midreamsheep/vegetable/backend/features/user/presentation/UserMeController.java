package indi.midreamsheep.vegetable.backend.features.user.presentation;

import indi.midreamsheep.vegetable.backend.common.api.ApiResponse;
import indi.midreamsheep.vegetable.backend.common.api.PageResponse;
import indi.midreamsheep.vegetable.backend.common.error.BizException;
import indi.midreamsheep.vegetable.backend.common.error.ErrorCode;
import indi.midreamsheep.vegetable.backend.features.collection.domain.CollectionDomainService;
import indi.midreamsheep.vegetable.backend.features.collection.domain.CollectionStatus;
import indi.midreamsheep.vegetable.backend.features.collection.domain.model.CollectionSummaryData;
import indi.midreamsheep.vegetable.backend.features.collection.presentation.dto.CollectionSummaryResponse;
import indi.midreamsheep.vegetable.backend.features.favorite.domain.FavoriteDomainService;
import indi.midreamsheep.vegetable.backend.features.like.domain.LikeDomainService;
import indi.midreamsheep.vegetable.backend.features.like.domain.LikeCommentDomainService;
import indi.midreamsheep.vegetable.backend.features.problem.domain.ProblemDomainService;
import indi.midreamsheep.vegetable.backend.features.problem.domain.ProblemStatus;
import indi.midreamsheep.vegetable.backend.features.problem.domain.model.ProblemSummaryData;
import indi.midreamsheep.vegetable.backend.features.problem.presentation.dto.ProblemAuthorResponse;
import indi.midreamsheep.vegetable.backend.features.problem.presentation.dto.ProblemTagResponse;
import indi.midreamsheep.vegetable.backend.features.problem.presentation.dto.ProblemSummaryResponse;
import indi.midreamsheep.vegetable.backend.features.tag.domain.model.TagData;
import indi.midreamsheep.vegetable.backend.features.tag.domain.port.TagRepositoryPort;
import indi.midreamsheep.vegetable.backend.features.comment.domain.model.ProblemCommentData;
import indi.midreamsheep.vegetable.backend.features.comment.presentation.dto.ProblemCommentResponse;
import indi.midreamsheep.vegetable.backend.features.user.domain.port.UserRepositoryPort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 当前登录用户的内容相关接口（我的题目/题单/收藏等）。
 */
@RestController
@RequestMapping("/api/v1/users/me")
public class UserMeController {

    private final ProblemDomainService problemDomainService;
    private final CollectionDomainService collectionDomainService;
    private final FavoriteDomainService favoriteDomainService;
    private final LikeDomainService likeDomainService;
    private final LikeCommentDomainService likeCommentDomainService;
    private final UserRepositoryPort userRepositoryPort;
    private final TagRepositoryPort tagRepositoryPort;

    /**
     * 构造当前用户内容控制器。
     *
     * @param problemDomainService 题目领域服务
     * @param collectionDomainService 题单领域服务
     * @param favoriteDomainService 收藏领域服务
     * @param likeDomainService 点赞领域服务
     * @param likeCommentDomainService 评论点赞领域服务
     * @param userRepositoryPort 用户仓储端口
     * @param tagRepositoryPort 标签仓储端口
     */
    public UserMeController(
            ProblemDomainService problemDomainService,
            CollectionDomainService collectionDomainService,
            FavoriteDomainService favoriteDomainService,
            LikeDomainService likeDomainService,
            LikeCommentDomainService likeCommentDomainService,
            UserRepositoryPort userRepositoryPort,
            TagRepositoryPort tagRepositoryPort
    ) {
        this.problemDomainService = problemDomainService;
        this.collectionDomainService = collectionDomainService;
        this.favoriteDomainService = favoriteDomainService;
        this.likeDomainService = likeDomainService;
        this.likeCommentDomainService = likeCommentDomainService;
        this.userRepositoryPort = userRepositoryPort;
        this.tagRepositoryPort = tagRepositoryPort;
    }

    /**
     * 我的题目列表（分页）。
     */
    @GetMapping("/problems")
    public ApiResponse<PageResponse<ProblemSummaryResponse>> myProblems(
            @RequestParam(value = "status", required = false) ProblemStatus status,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize
    ) {
        long userId = requireCurrentUserId();
        int finalPage = normalizePage(page);
        int finalPageSize = normalizePageSize(pageSize);
        PageResponse<ProblemSummaryData> result = problemDomainService.listByAuthor(userId, status, finalPage, finalPageSize);
        return ApiResponse.ok(mapProblemSummaryPage(result));
    }

    /**
     * 我的题单列表（分页）。
     */
    @GetMapping("/collections")
    public ApiResponse<PageResponse<CollectionSummaryResponse>> myCollections(
            @RequestParam(value = "status", required = false) CollectionStatus status,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize
    ) {
        long userId = requireCurrentUserId();
        int finalPage = normalizePage(page);
        int finalPageSize = normalizePageSize(pageSize);
        PageResponse<CollectionSummaryData> result = collectionDomainService.listByAuthor(userId, status, finalPage, finalPageSize);
        List<CollectionSummaryResponse> items = result.items().stream()
                .map(UserMeController::toCollectionSummaryResponse)
                .toList();
        return ApiResponse.ok(new PageResponse<>(items, result.page(), result.pageSize(), result.total()));
    }

    /**
     * 我收藏的题目（分页）。
     */
    @GetMapping("/favorites/problems")
    public ApiResponse<PageResponse<ProblemSummaryResponse>> myFavoriteProblems(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize
    ) {
        long userId = requireCurrentUserId();
        int finalPage = normalizePage(page);
        int finalPageSize = normalizePageSize(pageSize);
        PageResponse<ProblemSummaryData> result = favoriteDomainService.listFavoriteProblems(userId, finalPage, finalPageSize);
        return ApiResponse.ok(mapProblemSummaryPage(result));
    }

    /**
     * 我收藏的题单（分页）。
     */
    @GetMapping("/favorites/collections")
    public ApiResponse<PageResponse<CollectionSummaryResponse>> myFavoriteCollections(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize
    ) {
        long userId = requireCurrentUserId();
        int finalPage = normalizePage(page);
        int finalPageSize = normalizePageSize(pageSize);
        PageResponse<CollectionSummaryData> result = favoriteDomainService.listFavoriteCollections(userId, finalPage, finalPageSize);
        List<CollectionSummaryResponse> items = result.items().stream()
                .map(UserMeController::toCollectionSummaryResponse)
                .toList();
        return ApiResponse.ok(new PageResponse<>(items, result.page(), result.pageSize(), result.total()));
    }

    /**
     * 我点赞的题目（分页）。
     */
    @GetMapping("/likes/problems")
    public ApiResponse<PageResponse<ProblemSummaryResponse>> myLikedProblems(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize
    ) {
        long userId = requireCurrentUserId();
        int finalPage = normalizePage(page);
        int finalPageSize = normalizePageSize(pageSize);
        PageResponse<ProblemSummaryData> result = likeDomainService.listLikedProblems(userId, finalPage, finalPageSize);
        return ApiResponse.ok(mapProblemSummaryPage(result));
    }

    /**
     * 我点赞的评论（分页）。
     */
    @GetMapping("/likes/comments")
    public ApiResponse<PageResponse<ProblemCommentResponse>> myLikedComments(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize
    ) {
        long userId = requireCurrentUserId();
        int finalPage = normalizePage(page);
        int finalPageSize = normalizePageSize(pageSize);
        PageResponse<ProblemCommentData> result = likeCommentDomainService.listLikedComments(userId, finalPage, finalPageSize);
        List<ProblemCommentResponse> items = result.items().stream()
                .map(UserMeController::toCommentResponse)
                .toList();
        return ApiResponse.ok(new PageResponse<>(items, result.page(), result.pageSize(), result.total()));
    }

    /**
     * 将题目摘要分页结果映射为响应分页（补全 author 与 tags）。
     *
     * @param result 分页结果
     * @return 响应分页
     */
    private PageResponse<ProblemSummaryResponse> mapProblemSummaryPage(PageResponse<ProblemSummaryData> result) {
        Map<Long, ProblemTagResponse> tagMap = buildTagMap(result.items());
        List<ProblemSummaryResponse> items = result.items().stream()
                .map(data -> toProblemSummaryResponse(data, tagMap))
                .toList();
        return new PageResponse<>(items, result.page(), result.pageSize(), result.total());
    }

    /**
     * 将题目摘要转换为响应 DTO。
     *
     * @param data 题目摘要
     * @param tagMap 标签映射
     * @return 响应 DTO
     */
    private ProblemSummaryResponse toProblemSummaryResponse(ProblemSummaryData data, Map<Long, ProblemTagResponse> tagMap) {
        ProblemAuthorResponse author = buildAuthor(data.authorId());
        List<Long> tagIds = data.tagIds();
        List<ProblemTagResponse> tags = tagIds == null ? List.of() : tagIds.stream()
                .map(tagMap::get)
                .filter(Objects::nonNull)
                .toList();
        return new ProblemSummaryResponse(
                data.id(),
                data.title(),
                data.subject(),
                data.difficulty(),
                data.status(),
                data.visibility(),
                data.publishedAt(),
                author,
                tagIds == null ? List.of() : tagIds,
                tags
        );
    }

    /**
     * 构造标签ID -> 标签对象的映射（用于列表批量组装 tags）。
     *
     * @param items 题目摘要列表
     * @return 映射
     */
    private Map<Long, ProblemTagResponse> buildTagMap(List<ProblemSummaryData> items) {
        if (items == null || items.isEmpty()) {
            return Map.of();
        }
        Set<Long> ids = items.stream()
                .flatMap(it -> it.tagIds() == null ? java.util.stream.Stream.<Long>empty() : it.tagIds().stream())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (ids.isEmpty()) {
            return Map.of();
        }
        List<TagData> tags = tagRepositoryPort.findByIds(ids.stream().toList());
        return tags.stream().collect(Collectors.toMap(TagData::id, t -> new ProblemTagResponse(t.id(), t.name())));
    }

    /**
     * 构造题目作者信息（稳定可展示）。
     *
     * @param authorId 作者ID
     * @return 作者信息
     */
    private ProblemAuthorResponse buildAuthor(long authorId) {
        if (authorId <= 0) {
            return new ProblemAuthorResponse(0L, "用户 0", "用户 0");
        }
        return userRepositoryPort.findProfileById(authorId)
                .map(profile -> {
                    String preferredNickname = StringUtils.hasText(profile.nickname()) ? profile.nickname().trim() : null;
                    String preferredUsername = StringUtils.hasText(profile.username()) ? profile.username().trim() : null;
                    String displayName = StringUtils.hasText(preferredNickname)
                            ? preferredNickname
                            : (StringUtils.hasText(preferredUsername) ? preferredUsername : ("用户 " + authorId));
                    return new ProblemAuthorResponse(authorId, displayName, displayName);
                })
                .orElseGet(() -> new ProblemAuthorResponse(authorId, "用户 " + authorId, "用户 " + authorId));
    }

    /**
     * 将题单摘要转换为响应 DTO。
     *
     * @param data 题单摘要
     * @return 响应 DTO
     */
    private static CollectionSummaryResponse toCollectionSummaryResponse(CollectionSummaryData data) {
        return new CollectionSummaryResponse(
                data.id(),
                data.name(),
                data.description(),
                data.visibility(),
                data.status(),
                data.itemCount(),
                data.authorId()
        );
    }

    /**
     * 将评论数据转换为响应 DTO。
     *
     * @param data 评论数据
     * @return 响应 DTO
     */
    private static ProblemCommentResponse toCommentResponse(ProblemCommentData data) {
        return new ProblemCommentResponse(
                data.id(),
                data.problemId(),
                data.userId(),
                data.parentId(),
                data.replyToCommentId(),
                data.content(),
                data.likeCount(),
                data.deleted(),
                data.createdAt()
        );
    }

    /**
     * 规范化页码。
     *
     * @param page 页码
     * @return 页码
     */
    private static int normalizePage(Integer page) {
        if (page == null || page < 1) {
            throw new BizException(ErrorCode.BAD_REQUEST, "page 必须从 1 开始");
        }
        return page;
    }

    /**
     * 规范化页大小。
     *
     * @param pageSize 页大小
     * @return 页大小
     */
    private static int normalizePageSize(Integer pageSize) {
        if (pageSize == null) {
            return 20;
        }
        if (pageSize < 1 || pageSize > 100) {
            throw new BizException(ErrorCode.BAD_REQUEST, "pageSize 必须在 1-100 范围内");
        }
        return pageSize;
    }

    /**
     * 获取当前用户 ID。
     *
     * @return 用户ID
     */
    private static long requireCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "未登录");
        }
        Object principalObj = authentication.getPrincipal();
        String principal = (principalObj instanceof UserDetails userDetails)
                ? userDetails.getUsername()
                : String.valueOf(principalObj);
        try {
            long userId = Long.parseLong(principal);
            if (userId <= 0) {
                throw new NumberFormatException("id <= 0");
            }
            return userId;
        } catch (NumberFormatException ex) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "未登录");
        }
    }
}

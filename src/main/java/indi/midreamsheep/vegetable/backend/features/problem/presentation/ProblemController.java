package indi.midreamsheep.vegetable.backend.features.problem.presentation;

import indi.midreamsheep.vegetable.backend.common.api.ApiResponse;
import indi.midreamsheep.vegetable.backend.common.api.PageResponse;
import indi.midreamsheep.vegetable.backend.common.error.BizException;
import indi.midreamsheep.vegetable.backend.common.error.ErrorCode;
import indi.midreamsheep.vegetable.backend.features.problem.domain.ProblemDomainService;
import indi.midreamsheep.vegetable.backend.features.problem.domain.Visibility;
import indi.midreamsheep.vegetable.backend.features.problem.domain.command.ProblemCreateCommand;
import indi.midreamsheep.vegetable.backend.features.problem.domain.command.ProblemUpdateCommand;
import indi.midreamsheep.vegetable.backend.features.problem.domain.model.ProblemDetailData;
import indi.midreamsheep.vegetable.backend.features.problem.domain.model.ProblemSummaryData;
import indi.midreamsheep.vegetable.backend.features.problem.domain.query.ProblemQuery;
import indi.midreamsheep.vegetable.backend.features.problem.presentation.dto.ProblemCreateRequest;
import indi.midreamsheep.vegetable.backend.features.problem.presentation.dto.ProblemCreateResponse;
import indi.midreamsheep.vegetable.backend.features.problem.presentation.dto.ProblemDetailResponse;
import indi.midreamsheep.vegetable.backend.features.problem.presentation.dto.ProblemPublishRequest;
import indi.midreamsheep.vegetable.backend.features.problem.presentation.dto.ProblemStatusResponse;
import indi.midreamsheep.vegetable.backend.features.problem.presentation.dto.ProblemSummaryResponse;
import indi.midreamsheep.vegetable.backend.features.problem.presentation.dto.ProblemUpdateRequest;
import indi.midreamsheep.vegetable.backend.features.problem.presentation.dto.ProblemAuthorResponse;
import indi.midreamsheep.vegetable.backend.features.problem.presentation.dto.ProblemTagResponse;
import indi.midreamsheep.vegetable.backend.features.favorite.domain.FavoriteDomainService;
import indi.midreamsheep.vegetable.backend.features.like.domain.LikeDomainService;
import indi.midreamsheep.vegetable.backend.features.tag.domain.model.TagData;
import indi.midreamsheep.vegetable.backend.features.tag.domain.port.TagRepositoryPort;
import indi.midreamsheep.vegetable.backend.features.user.domain.port.UserRepositoryPort;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 题目相关接口（提交/更新等）。
 */
@RestController
@RequestMapping("/api/v1/problems")
public class ProblemController {

    private final ProblemDomainService problemDomainService;
    private final FavoriteDomainService favoriteDomainService;
    private final LikeDomainService likeDomainService;
    private final UserRepositoryPort userRepositoryPort;
    private final TagRepositoryPort tagRepositoryPort;

    /**
     * 构造题目控制器。
     *
     * @param problemDomainService 题目领域服务
     * @param favoriteDomainService 收藏领域服务
     * @param likeDomainService 点赞领域服务
     * @param userRepositoryPort 用户仓储端口
     * @param tagRepositoryPort 标签仓储端口
     */
    public ProblemController(
            ProblemDomainService problemDomainService,
            FavoriteDomainService favoriteDomainService,
            LikeDomainService likeDomainService,
            UserRepositoryPort userRepositoryPort,
            TagRepositoryPort tagRepositoryPort
    ) {
        this.problemDomainService = problemDomainService;
        this.favoriteDomainService = favoriteDomainService;
        this.likeDomainService = likeDomainService;
        this.userRepositoryPort = userRepositoryPort;
        this.tagRepositoryPort = tagRepositoryPort;
    }

    /**
     * 创建题目（默认草稿）。
     *
     * @param request 创建请求
     * @return 统一响应体（题目ID与状态）
     */
    @PostMapping
    public ApiResponse<ProblemCreateResponse> create(@Valid @RequestBody ProblemCreateRequest request) {
        long authorId = requireCurrentUserId();

        String solution = normalizeOptionalText(request.solution());
        ProblemCreateCommand command = new ProblemCreateCommand(
                authorId,
                request.title(),
                request.subject().trim(),
                request.difficulty(),
                request.statementFormat(),
                request.statement(),
                request.solutionFormat(),
                solution,
                request.visibility(),
                request.visibility() == Visibility.UNLISTED ? ProblemDomainService.generateShareKey() : null,
                request.tagIds()
        );

        long id = problemDomainService.create(command);
        return ApiResponse.ok(new ProblemCreateResponse(
                id,
                indi.midreamsheep.vegetable.backend.features.problem.domain.ProblemStatus.DRAFT,
                request.visibility(),
                command.shareKey()
        ));
    }

    /**
     * 获取公开题目列表（分页）。
     *
     * @param subject 学科
     * @param difficultyMin 难度下限
     * @param difficultyMax 难度上限
     * @param keyword 关键字（标题）
     * @param page 页码
     * @param pageSize 每页大小
     * @return 统一响应体（分页结果）
     */
    @GetMapping
    public ApiResponse<PageResponse<ProblemSummaryResponse>> list(
            @RequestParam(value = "subject", required = false) String subject,
            @RequestParam(value = "tagIds", required = false) List<Long> tagIds,
            @RequestParam(value = "difficultyMin", required = false) Integer difficultyMin,
            @RequestParam(value = "difficultyMax", required = false) Integer difficultyMax,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize
    ) {
        int finalPage = normalizePage(page);
        int finalPageSize = normalizePageSize(pageSize);
        validateDifficultyRange(difficultyMin, difficultyMax);
        List<Long> finalTagIds = normalizeOptionalIds(tagIds, "tagIds");
        String normalizedSort = normalizeSort(sort);

        ProblemQuery query = new ProblemQuery(
                subject,
                finalTagIds,
                difficultyMin,
                difficultyMax,
                keyword,
                normalizedSort,
                finalPage,
                finalPageSize
        );
        PageResponse<ProblemSummaryData> result = problemDomainService.listPublic(query);
        Map<Long, ProblemTagResponse> tagMap = buildTagMap(result.items());
        List<ProblemSummaryResponse> items = result.items().stream()
                .map(data -> toSummaryResponse(data, tagMap))
                .toList();
        return ApiResponse.ok(new PageResponse<>(items, result.page(), result.pageSize(), result.total()));
    }

    /**
     * 获取题目详情（按可见性校验）。
     *
     * @param id 题目ID
     * @return 统一响应体（题目详情）
     */
    @GetMapping("/{id}")
    public ApiResponse<ProblemDetailResponse> detail(@PathVariable("id") long id) {
        Long requesterId = currentUserIdOrNull();
        ProblemDetailData detail = problemDomainService.getDetail(id, requesterId);
        ProblemAuthorResponse author = buildAuthor(detail.authorId());
        List<ProblemTagResponse> tags = buildTags(detail.tagIds());
        return ApiResponse.ok(toDetailResponse(detail, author, tags));
    }

    /**
     * 通过分享 key 获取题目详情。
     *
     * @param shareKey 分享 key
     * @return 统一响应体（题目详情）
     */
    @GetMapping("/share/{shareKey}")
    public ApiResponse<ProblemDetailResponse> detailByShareKey(@PathVariable("shareKey") String shareKey) {
        if (!StringUtils.hasText(shareKey)) {
            throw new BizException(ErrorCode.BAD_REQUEST, "shareKey 不能为空");
        }
        ProblemDetailData detail = problemDomainService.getDetailByShareKey(shareKey);
        ProblemAuthorResponse author = buildAuthor(detail.authorId());
        List<ProblemTagResponse> tags = buildTags(detail.tagIds());
        return ApiResponse.ok(toDetailResponse(detail, author, tags));
    }

    /**
     * 收藏题目。
     *
     * @param id 题目ID
     * @return 统一响应体
     */
    @PostMapping("/{id}/favorite")
    public ApiResponse<Void> favorite(@PathVariable("id") long id) {
        long userId = requireCurrentUserId();
        favoriteDomainService.favoriteProblem(userId, id);
        return ApiResponse.ok();
    }

    /**
     * 取消收藏题目。
     *
     * @param id 题目ID
     * @return 统一响应体
     */
    @DeleteMapping("/{id}/favorite")
    public ApiResponse<Void> unfavorite(@PathVariable("id") long id) {
        long userId = requireCurrentUserId();
        favoriteDomainService.unfavoriteProblem(userId, id);
        return ApiResponse.ok();
    }

    /**
     * 点赞题目。
     *
     * @param id 题目ID
     * @return 统一响应体
     */
    @PostMapping("/{id}/like")
    public ApiResponse<Void> like(@PathVariable("id") long id) {
        long userId = requireCurrentUserId();
        likeDomainService.likeProblem(userId, id);
        return ApiResponse.ok();
    }

    /**
     * 取消点赞题目。
     *
     * @param id 题目ID
     * @return 统一响应体
     */
    @DeleteMapping("/{id}/like")
    public ApiResponse<Void> unlike(@PathVariable("id") long id) {
        long userId = requireCurrentUserId();
        likeDomainService.unlikeProblem(userId, id);
        return ApiResponse.ok();
    }

    /**
     * 更新题目内容。
     *
     * @param id 题目ID
     * @param request 更新请求
     * @return 统一响应体（更新后的状态）
     */
    @PutMapping("/{id}")
    public ApiResponse<ProblemStatusResponse> update(
            @PathVariable("id") long id,
            @Valid @RequestBody ProblemUpdateRequest request
    ) {
        long authorId = requireCurrentUserId();
        String solution = normalizeOptionalText(request.solution());
        ProblemUpdateCommand command = new ProblemUpdateCommand(
                id,
                authorId,
                request.title(),
                request.subject().trim(),
                request.difficulty(),
                request.statementFormat(),
                request.statement(),
                request.solutionFormat(),
                solution,
                request.visibility(),
                null,
                null,
                null,
                request.tagIds()
        );
        ProblemDetailData updated = problemDomainService.update(command);
        return ApiResponse.ok(new ProblemStatusResponse(
                updated.id(),
                updated.status(),
                updated.visibility(),
                updated.shareKey()
        ));
    }

    /**
     * 发布题目。
     *
     * @param id 题目ID
     * @return 统一响应体（发布后的状态）
     */
    @PostMapping("/{id}/publish")
    public ApiResponse<ProblemStatusResponse> publish(
            @PathVariable("id") long id,
            @Valid @RequestBody(required = false) ProblemPublishRequest request
    ) {
        long authorId = requireCurrentUserId();
        ProblemDetailData updated = problemDomainService.publish(
                id,
                authorId,
                request == null ? null : request.subject(),
                request == null ? null : request.tagIds(),
                request == null ? null : request.newTags()
        );
        return ApiResponse.ok(new ProblemStatusResponse(
                updated.id(),
                updated.status(),
                updated.visibility(),
                updated.shareKey()
        ));
    }

    /**
     * 下架题目（作者）。
     *
     * @param id 题目ID
     * @return 统一响应体（更新后的状态）
     */
    @PostMapping("/{id}/disable")
    public ApiResponse<ProblemStatusResponse> disable(@PathVariable("id") long id) {
        long authorId = requireCurrentUserId();
        ProblemDetailData updated = problemDomainService.disableByAuthor(id, authorId);
        return ApiResponse.ok(new ProblemStatusResponse(
                updated.id(),
                updated.status(),
                updated.visibility(),
                updated.shareKey()
        ));
    }

    /**
     * 删除草稿题目（软删除）。
     *
     * @param id 题目ID
     * @return 统一响应体
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") long id) {
        long authorId = requireCurrentUserId();
        problemDomainService.deleteDraft(id, authorId);
        return ApiResponse.ok();
    }

    /**
     * 获取当前用户 ID（从认证上下文获取）。
     *
     * @return 用户ID
     */
    private static long requireCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "未认证用户");
        }
        Object principalObj = authentication.getPrincipal();
        String principal = (principalObj instanceof UserDetails userDetails)
                ? userDetails.getUsername()
                : String.valueOf(principalObj);
        try {
            return Long.parseLong(principal);
        } catch (NumberFormatException ex) {
            throw new BizException(ErrorCode.BAD_REQUEST, "用户ID不合法");
        }
    }

    /**
     * 尝试获取当前用户 ID（未登录时返回 null）。
     *
     * @return 用户ID 或 null
     */
    private static Long currentUserIdOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }
        Object principalObj = authentication.getPrincipal();
        String principal = (principalObj instanceof UserDetails userDetails)
                ? userDetails.getUsername()
                : String.valueOf(principalObj);
        try {
            long userId = Long.parseLong(principal);
            return userId > 0 ? userId : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * 规范化可选文本：为空白则返回 null。
     *
     * @param text 文本
     * @return 规范化后的文本
     */
    private static String normalizeOptionalText(String text) {
        if (!StringUtils.hasText(text)) {
            return null;
        }
        return text;
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
     * 校验难度范围。
     *
     * @param min 最小难度
     * @param max 最大难度
     */
    private static void validateDifficultyRange(Integer min, Integer max) {
        if (min != null && (min < 1 || min > 5)) {
            throw new BizException(ErrorCode.BAD_REQUEST, "difficultyMin 范围为 1-5");
        }
        if (max != null && (max < 1 || max > 5)) {
            throw new BizException(ErrorCode.BAD_REQUEST, "difficultyMax 范围为 1-5");
        }
        if (min != null && max != null && min > max) {
            throw new BizException(ErrorCode.BAD_REQUEST, "difficultyMin 不能大于 difficultyMax");
        }
    }

    /**
     * 规范化可选 ID 列表：过滤空值、去重、排序，并校验正整数。
     *
     * @param name 参数名
     * @param ids ID列表
     * @return 规范化后的ID列表（为空时返回空列表）
     */
    private static List<Long> normalizeOptionalIds(List<Long> ids, String name) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        Set<Long> unique = new java.util.HashSet<>();
        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            if (id <= 0) {
                throw new BizException(ErrorCode.BAD_REQUEST, name + " 包含不合法的 ID");
            }
            unique.add(id);
        }
        return unique.stream().sorted().toList();
    }

    /**
     * 规范化排序参数。
     *
     * @param sort 排序参数
     * @return 规范化后的排序参数
     */
    private static String normalizeSort(String sort) {
        if (!StringUtils.hasText(sort)) {
            return null;
        }
        String value = sort.trim().toUpperCase();
        return switch (value) {
            case "LATEST", "PUBLISHED_AT", "DIFFICULTY", "HOT" -> value;
            default -> throw new BizException(ErrorCode.BAD_REQUEST, "sort 不合法");
        };
    }

    /**
     * 将领域数据转换为摘要响应 DTO。
     *
     * @param data 摘要数据
     * @return 摘要响应
     */
    private ProblemSummaryResponse toSummaryResponse(ProblemSummaryData data, Map<Long, ProblemTagResponse> tagMap) {
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
     * 将领域数据转换为详情响应 DTO。
     *
     * @param data 详情数据
     * @return 详情响应
     */
    private static ProblemDetailResponse toDetailResponse(
            ProblemDetailData data,
            ProblemAuthorResponse author,
            List<ProblemTagResponse> tags
    ) {
        return new ProblemDetailResponse(
                data.id(),
                data.title(),
                data.subject(),
                data.difficulty(),
                data.statementFormat(),
                data.statementContent(),
                data.solutionFormat(),
                data.solutionContent(),
                data.visibility(),
                data.shareKey(),
                data.status(),
                author,
                data.tagIds(),
                tags
        );
    }

    /**
     * 构造题目作者信息。
     * <p>
     * nickname 可能为空，displayName 为服务端兜底后的稳定可展示字段。
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
     * 根据标签ID列表构造标签对象列表。
     *
     * @param tagIds 标签ID列表
     * @return 标签对象列表
     */
    private List<ProblemTagResponse> buildTags(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return List.of();
        }
        List<TagData> tags = tagRepositoryPort.findByIds(tagIds);
        Map<Long, String> idToName = tags.stream().collect(Collectors.toMap(TagData::id, TagData::name));
        return tagIds.stream()
                .map(id -> {
                    String name = idToName.get(id);
                    return name == null ? null : new ProblemTagResponse(id, name);
                })
                .filter(Objects::nonNull)
                .toList();
    }
}

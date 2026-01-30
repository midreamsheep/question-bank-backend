package indi.midreamsheep.vegetable.backend.features.problem.domain;

import indi.midreamsheep.vegetable.backend.common.error.BizException;
import indi.midreamsheep.vegetable.backend.common.error.ErrorCode;
import indi.midreamsheep.vegetable.backend.features.problem.domain.command.ProblemCreateCommand;
import indi.midreamsheep.vegetable.backend.features.problem.domain.command.ProblemUpdateCommand;
import indi.midreamsheep.vegetable.backend.features.problem.domain.model.ProblemDetailData;
import indi.midreamsheep.vegetable.backend.features.problem.domain.model.ProblemSummaryData;
import indi.midreamsheep.vegetable.backend.features.problem.domain.port.ProblemRepositoryPort;
import indi.midreamsheep.vegetable.backend.features.problem.domain.query.ProblemQuery;
import indi.midreamsheep.vegetable.backend.common.api.PageResponse;
import indi.midreamsheep.vegetable.backend.features.tag.domain.TagDomainService;
import indi.midreamsheep.vegetable.backend.features.tag.domain.command.TagCreateCommand;
import indi.midreamsheep.vegetable.backend.features.tag.domain.model.TagData;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 题目领域服务：封装题目创建与提交相关的核心业务逻辑。
 */
public class ProblemDomainService {

    private final ProblemRepositoryPort problemRepositoryPort;
    private final TagDomainService tagDomainService;

    /**
     * 构造题目领域服务。
     *
     * @param problemRepositoryPort 题目仓储端口
     * @param tagDomainService 标签领域服务
     */
    public ProblemDomainService(ProblemRepositoryPort problemRepositoryPort, TagDomainService tagDomainService) {
        this.problemRepositoryPort = problemRepositoryPort;
        this.tagDomainService = tagDomainService;
    }

    /**
     * 创建题目（默认创建草稿），并返回题目ID。
     *
     * @param command 创建命令
     * @return 题目ID
     */
    public long create(ProblemCreateCommand command) {
        validate(command);
        ProblemCreateCommand finalCommand = command;
        if (command.visibility() == Visibility.UNLISTED && !StringUtils.hasText(command.shareKey())) {
            finalCommand = new ProblemCreateCommand(
                    command.authorId(),
                    command.title(),
                    command.subject(),
                    command.difficulty(),
                    command.statementFormat(),
                    command.statementContent(),
                    command.solutionFormat(),
                    command.solutionContent(),
                    command.visibility(),
                    generateShareKey(),
                    command.tagIds()
            );
        }
        return problemRepositoryPort.create(finalCommand);
    }

    /**
     * 查询公开题目列表（分页）。
     *
     * @param query 查询条件
     * @return 分页结果
     */
    public PageResponse<ProblemSummaryData> listPublic(ProblemQuery query) {
        return problemRepositoryPort.listPublic(query);
    }

    /**
     * 我的题目列表（作者维度）。
     *
     * @param authorId 作者ID
     * @param status 状态筛选（可为空）
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public PageResponse<ProblemSummaryData> listByAuthor(long authorId, ProblemStatus status, int page, int pageSize) {
        if (authorId <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "authorId 不合法");
        }
        if (page < 1) {
            throw new BizException(ErrorCode.BAD_REQUEST, "page 必须从 1 开始");
        }
        if (pageSize < 1 || pageSize > 100) {
            throw new BizException(ErrorCode.BAD_REQUEST, "pageSize 必须在 1-100 范围内");
        }
        return problemRepositoryPort.listByAuthor(authorId, status, page, pageSize);
    }

    /**
     * 获取题目详情（按可见性校验）。
     *
     * @param id 题目ID
     * @param requesterId 访问者ID（可为空）
     * @return 题目详情
     */
    public ProblemDetailData getDetail(long id, Long requesterId) {
        ProblemDetailData detail = problemRepositoryPort.findById(id)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "题目不存在"));
        if (detail.visibility() == Visibility.PUBLIC && detail.status() == ProblemStatus.PUBLISHED) {
            if (requesterId == null || requesterId != detail.authorId()) {
                problemRepositoryPort.incrementViewCount(id);
            }
            return detail;
        }
        if (requesterId != null && requesterId == detail.authorId()) {
            return detail;
        }
        throw new BizException(ErrorCode.FORBIDDEN, "无权限访问该题目");
    }

    /**
     * 通过分享 key 获取题目详情（仅 UNLISTED + PUBLISHED）。
     *
     * @param shareKey 分享 key
     * @return 题目详情
     */
    public ProblemDetailData getDetailByShareKey(String shareKey) {
        ProblemDetailData detail = problemRepositoryPort.findByShareKey(shareKey)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "题目不存在"));
        if (detail.visibility() != Visibility.UNLISTED || detail.status() != ProblemStatus.PUBLISHED) {
            throw new BizException(ErrorCode.NOT_FOUND, "题目不存在");
        }
        problemRepositoryPort.incrementViewCount(detail.id());
        return detail;
    }

    /**
     * 更新题目内容。
     *
     * @param command 更新命令
     * @return 更新后的题目详情
     */
    public ProblemDetailData update(ProblemUpdateCommand command) {
        validate(command);
        ProblemDetailData existing = problemRepositoryPort.findById(command.id())
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "题目不存在"));
        if (existing.authorId() != command.authorId()) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权限更新该题目");
        }
        String shareKey = resolveShareKey(command.visibility(), existing.shareKey());
        ProblemUpdateCommand finalCommand = new ProblemUpdateCommand(
                command.id(),
                existing.authorId(),
                command.title(),
                command.subject(),
                command.difficulty(),
                command.statementFormat(),
                command.statementContent(),
                command.solutionFormat(),
                command.solutionContent(),
                command.visibility(),
                shareKey,
                existing.status(),
                existing.publishedAt(),
                command.tagIds()
        );
        return problemRepositoryPort.update(finalCommand);
    }

    /**
     * 发布题目。
     *
     * @param id 题目ID
     * @param requesterId 请求用户ID
     * @return 发布后的题目详情
     */
    public ProblemDetailData publish(
            long id,
            long requesterId,
            String subject,
            List<Long> tagIds,
            List<String> newTags
    ) {
        ProblemDetailData existing = problemRepositoryPort.findById(id)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "题目不存在"));
        if (existing.authorId() != requesterId) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权限发布该题目");
        }
        String finalSubject = StringUtils.hasText(subject) ? subject.trim() : existing.subject();
        if (!StringUtils.hasText(finalSubject)) {
            throw new BizException(ErrorCode.BAD_REQUEST, "subject 不能为空");
        }

        List<Long> finalTagIds = resolveFinalTagIds(finalSubject, tagIds, newTags, existing.tagIds());

        // Publish 操作中一并把 subject/tags 落库，保证发布后的题目数据一致。
        ProblemUpdateCommand updateCommand = new ProblemUpdateCommand(
                existing.id(),
                existing.authorId(),
                existing.title(),
                finalSubject,
                existing.difficulty(),
                existing.statementFormat(),
                existing.statementContent(),
                existing.solutionFormat(),
                existing.solutionContent(),
                existing.visibility(),
                existing.shareKey(),
                existing.status(),
                existing.publishedAt(),
                finalTagIds
        );
        problemRepositoryPort.update(updateCommand);

        if (existing.status() == ProblemStatus.PUBLISHED) {
            return problemRepositoryPort.findById(id)
                    .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "题目不存在"));
        }
        return problemRepositoryPort.publish(id);
    }

    /**
     * 解析发布时的最终标签列表：支持新增标签名称，并合并到最终 tagIds。
     *
     * @param subject 学科
     * @param requestedTagIds 请求中指定的标签ID列表（可为空）
     * @param newTags 请求中指定的新标签名称列表（可为空）
     * @param existingTagIds 草稿中已有的标签ID列表（可为空）
     * @return 最终标签ID列表（去重、按升序）
     */
    private List<Long> resolveFinalTagIds(
            String subject,
            List<Long> requestedTagIds,
            List<String> newTags,
            List<Long> existingTagIds
    ) {
        List<Long> base = requestedTagIds == null ? existingTagIds : requestedTagIds;
        Set<Long> merged = new HashSet<>();
        if (base != null) {
            for (Long id : base) {
                if (id == null || id <= 0) {
                    throw new BizException(ErrorCode.BAD_REQUEST, "tagIds 包含不合法的 ID");
                }
                merged.add(id);
            }
        }
        if (newTags != null && !newTags.isEmpty()) {
            for (String raw : newTags) {
                if (!StringUtils.hasText(raw)) {
                    throw new BizException(ErrorCode.BAD_REQUEST, "newTags 包含空标签名");
                }
                String name = raw.trim();
                TagData data = tagDomainService.getOrCreate(new TagCreateCommand(subject, name));
                merged.add(data.id());
            }
        }
        return merged.stream().sorted().toList();
    }

    /**
     * 下架题目（管理员）。
     *
     * @param id 题目ID
     * @return 更新后的题目详情
     */
    public ProblemDetailData disable(long id) {
        problemRepositoryPort.findById(id)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "题目不存在"));
        return problemRepositoryPort.disable(id);
    }

    /**
     * 作者下架自己的题目。
     *
     * @param id 题目ID
     * @param requesterId 请求用户ID
     * @return 更新后的题目详情
     */
    public ProblemDetailData disableByAuthor(long id, long requesterId) {
        ProblemDetailData existing = problemRepositoryPort.findById(id)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "题目不存在"));
        if (existing.authorId() != requesterId) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权限下架该题目");
        }
        if (existing.status() == ProblemStatus.DISABLED) {
            return existing;
        }
        if (existing.status() == ProblemStatus.DRAFT) {
            throw new BizException(ErrorCode.BAD_REQUEST, "草稿题目无需下架，可直接删除");
        }
        return problemRepositoryPort.disable(id);
    }

    /**
     * 删除草稿题目（作者）。
     *
     * @param id 题目ID
     * @param requesterId 请求用户ID
     */
    public void deleteDraft(long id, long requesterId) {
        ProblemDetailData existing = problemRepositoryPort.findById(id)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "题目不存在"));
        if (existing.authorId() != requesterId) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权限删除该题目");
        }
        if (existing.status() != ProblemStatus.DRAFT) {
            throw new BizException(ErrorCode.BAD_REQUEST, "仅可删除草稿题目");
        }
        problemRepositoryPort.softDelete(id);
    }

    /**
     * 若可见性为 UNLISTED，则生成 shareKey。
     *
     * @return shareKey
     */
    public static String generateShareKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 校验创建命令的合法性。
     *
     * @param command 创建命令
     */
    private static void validate(ProblemCreateCommand command) {
        if (command == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "请求不能为空");
        }
        if (command.authorId() <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "authorId 不合法");
        }
        if (!StringUtils.hasText(command.title())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "标题不能为空");
        }
        if (command.difficulty() < 1 || command.difficulty() > 5) {
            throw new BizException(ErrorCode.BAD_REQUEST, "难度范围为 1-5");
        }
        if (!StringUtils.hasText(command.subject())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "subject 不能为空");
        }
        if (command.statementFormat() == null || !StringUtils.hasText(command.statementContent())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "题干不能为空");
        }
        if (command.solutionContent() != null && command.solutionFormat() == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "solutionFormat 不能为空");
        }
        if (command.visibility() == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "visibility 不能为空");
        }
        if (command.visibility() == Visibility.UNLISTED
                && StringUtils.hasText(command.shareKey())
                && command.shareKey().length() < 16) {
            throw new BizException(ErrorCode.BAD_REQUEST, "shareKey 不合法");
        }
        validateRelationIds(command.tagIds(), "tagIds");
    }

    /**
     * 校验更新命令的合法性。
     *
     * @param command 更新命令
     */
    private static void validate(ProblemUpdateCommand command) {
        if (command == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "请求不能为空");
        }
        if (command.id() <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "id 不合法");
        }
        if (command.authorId() <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "authorId 不合法");
        }
        if (!StringUtils.hasText(command.title())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "标题不能为空");
        }
        if (command.difficulty() < 1 || command.difficulty() > 5) {
            throw new BizException(ErrorCode.BAD_REQUEST, "难度范围为 1-5");
        }
        if (!StringUtils.hasText(command.subject())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "subject 不能为空");
        }
        if (command.statementFormat() == null || !StringUtils.hasText(command.statementContent())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "题干不能为空");
        }
        if (command.solutionContent() != null && command.solutionFormat() == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "solutionFormat 不能为空");
        }
        if (command.visibility() == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "visibility 不能为空");
        }
        validateRelationIds(command.tagIds(), "tagIds");
    }

    /**
     * 处理 UNLISTED 分享 key。
     *
     * @param visibility 可见性
     * @param existingShareKey 现有 shareKey
     * @return 处理后的 shareKey
     */
    private static String resolveShareKey(Visibility visibility, String existingShareKey) {
        if (visibility != Visibility.UNLISTED) {
            return null;
        }
        if (StringUtils.hasText(existingShareKey)) {
            return existingShareKey;
        }
        return generateShareKey();
    }

    /**
     * 校验关联 ID 列表。
     *
     * @param ids 关联ID列表
     * @param name 参数名
     */
    private static void validateRelationIds(List<Long> ids, String name) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        Set<Long> unique = new HashSet<>();
        for (Long id : ids) {
            if (id == null || id <= 0) {
                throw new BizException(ErrorCode.BAD_REQUEST, name + " 包含不合法的 ID");
            }
            if (!unique.add(id)) {
                throw new BizException(ErrorCode.BAD_REQUEST, name + " 包含重复 ID");
            }
        }
    }
}

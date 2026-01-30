package indi.midreamsheep.vegetable.backend.features.collection.domain;

import indi.midreamsheep.vegetable.backend.common.api.PageResponse;
import indi.midreamsheep.vegetable.backend.common.error.BizException;
import indi.midreamsheep.vegetable.backend.common.error.ErrorCode;
import indi.midreamsheep.vegetable.backend.features.collection.domain.command.CollectionAddItemCommand;
import indi.midreamsheep.vegetable.backend.features.collection.domain.command.CollectionCreateCommand;
import indi.midreamsheep.vegetable.backend.features.collection.domain.command.CollectionRemoveItemCommand;
import indi.midreamsheep.vegetable.backend.features.collection.domain.command.CollectionReorderCommand;
import indi.midreamsheep.vegetable.backend.features.collection.domain.command.CollectionUpdateCommand;
import indi.midreamsheep.vegetable.backend.features.collection.domain.model.CollectionDetailData;
import indi.midreamsheep.vegetable.backend.features.collection.domain.model.CollectionItemData;
import indi.midreamsheep.vegetable.backend.features.collection.domain.model.CollectionSummaryData;
import indi.midreamsheep.vegetable.backend.features.collection.domain.port.CollectionRepositoryPort;
import indi.midreamsheep.vegetable.backend.features.problem.domain.Visibility;
import indi.midreamsheep.vegetable.backend.features.problem.domain.port.ProblemRepositoryPort;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 题单领域服务。
 */
public class CollectionDomainService {

    private final CollectionRepositoryPort collectionRepositoryPort;
    private final ProblemRepositoryPort problemRepositoryPort;

    /**
     * 构造题单领域服务。
     *
     * @param collectionRepositoryPort 题单仓储端口
     * @param problemRepositoryPort 题目仓储端口
     */
    public CollectionDomainService(
            CollectionRepositoryPort collectionRepositoryPort,
            ProblemRepositoryPort problemRepositoryPort
    ) {
        this.collectionRepositoryPort = collectionRepositoryPort;
        this.problemRepositoryPort = problemRepositoryPort;
    }

    /**
     * 创建题单。
     *
     * @param command 创建命令
     * @return 题单ID
     */
    public long create(CollectionCreateCommand command) {
        validate(command);
        CollectionCreateCommand finalCommand = command;
        if (command.visibility() == Visibility.UNLISTED && !StringUtils.hasText(command.shareKey())) {
            finalCommand = new CollectionCreateCommand(
                    command.authorId(),
                    command.name(),
                    command.description(),
                    command.visibility(),
                    generateShareKey()
            );
        }
        return collectionRepositoryPort.create(finalCommand);
    }

    /**
     * 查询公开题单列表（分页）。
     *
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public PageResponse<CollectionSummaryData> listPublic(int page, int pageSize) {
        return collectionRepositoryPort.listPublic(page, pageSize);
    }

    /**
     * 我的题单列表（作者维度）。
     *
     * @param authorId 作者ID
     * @param status 状态筛选（可为空）
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public PageResponse<CollectionSummaryData> listByAuthor(long authorId, CollectionStatus status, int page, int pageSize) {
        if (authorId <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "authorId 不合法");
        }
        if (page < 1) {
            throw new BizException(ErrorCode.BAD_REQUEST, "page 必须从 1 开始");
        }
        if (pageSize < 1 || pageSize > 100) {
            throw new BizException(ErrorCode.BAD_REQUEST, "pageSize 必须在 1-100 范围内");
        }
        return collectionRepositoryPort.listByAuthor(authorId, status, page, pageSize);
    }

    /**
     * 获取题单详情（按可见性校验）。
     *
     * @param id 题单ID
     * @param requesterId 访问者ID
     * @return 题单详情
     */
    public CollectionDetailData getDetail(long id, Long requesterId) {
        CollectionDetailData detail = collectionRepositoryPort.findDetailById(id)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "题单不存在"));
        if (detail.status() == CollectionStatus.ACTIVE && detail.visibility() == Visibility.PUBLIC) {
            return detail;
        }
        if (requesterId != null && requesterId == detail.authorId()) {
            return detail;
        }
        throw new BizException(ErrorCode.FORBIDDEN, "无权限访问该题单");
    }

    /**
     * 通过分享 key 获取题单详情。
     *
     * @param shareKey 分享 key
     * @return 题单详情
     */
    public CollectionDetailData getDetailByShareKey(String shareKey) {
        CollectionDetailData detail = collectionRepositoryPort.findDetailByShareKey(shareKey)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "题单不存在"));
        if (detail.status() != CollectionStatus.ACTIVE || detail.visibility() != Visibility.UNLISTED) {
            throw new BizException(ErrorCode.NOT_FOUND, "题单不存在");
        }
        return detail;
    }

    /**
     * 更新题单信息（作者）。
     *
     * @param command 更新命令
     * @return 更新后的题单详情
     */
    public CollectionDetailData update(CollectionUpdateCommand command) {
        validate(command);
        CollectionDetailData existing = collectionRepositoryPort.findDetailById(command.id())
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "题单不存在"));
        if (existing.authorId() != command.authorId()) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权限编辑该题单");
        }
        String finalShareKey = existing.shareKey();
        if (command.visibility() == Visibility.UNLISTED && !StringUtils.hasText(finalShareKey)) {
            finalShareKey = generateShareKey();
        }
        if (command.visibility() == Visibility.UNLISTED && StringUtils.hasText(command.shareKey())) {
            finalShareKey = command.shareKey();
        }
        CollectionUpdateCommand finalCommand = new CollectionUpdateCommand(
                command.id(),
                command.authorId(),
                command.name(),
                command.description(),
                command.visibility(),
                finalShareKey
        );
        return collectionRepositoryPort.update(finalCommand);
    }

    /**
     * 删除题单（作者，软删除）。
     *
     * @param id 题单ID
     * @param authorId 作者ID
     */
    public void delete(long id, long authorId) {
        if (id <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "id 不合法");
        }
        if (authorId <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "authorId 不合法");
        }
        CollectionDetailData existing = collectionRepositoryPort.findDetailById(id)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "题单不存在"));
        if (existing.authorId() != authorId) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权限删除该题单");
        }
        collectionRepositoryPort.softDelete(id);
    }

    /**
     * 添加题单条目。
     *
     * @param command 添加命令
     */
    public void addItem(CollectionAddItemCommand command) {
        validate(command);
        CollectionDetailData detail = collectionRepositoryPort.findDetailById(command.collectionId())
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "题单不存在"));
        if (detail.authorId() != command.authorId()) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权限编辑该题单");
        }
        if (collectionRepositoryPort.existsItem(command.collectionId(), command.problemId())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "题目已存在于题单中");
        }
        problemRepositoryPort.findById(command.problemId())
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "题目不存在"));
        collectionRepositoryPort.addItem(command.collectionId(), command.problemId(), command.sortOrder());
    }

    /**
     * 调整题单条目顺序。
     *
     * @param command 调整命令
     */
    public void reorder(CollectionReorderCommand command) {
        validate(command);
        CollectionDetailData detail = collectionRepositoryPort.findDetailById(command.collectionId())
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "题单不存在"));
        if (detail.authorId() != command.authorId()) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权限编辑该题单");
        }
        collectionRepositoryPort.updateItemOrders(command.collectionId(), command.items());
    }

    /**
     * 从题单移除题目（作者）。
     *
     * @param command 移除命令
     */
    public void removeItem(CollectionRemoveItemCommand command) {
        validate(command);
        CollectionDetailData detail = collectionRepositoryPort.findDetailById(command.collectionId())
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "题单不存在"));
        if (detail.authorId() != command.authorId()) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权限编辑该题单");
        }
        collectionRepositoryPort.removeItem(command.collectionId(), command.problemId());
    }

    /**
     * 生成分享 key。
     *
     * @return 分享 key
     */
    public static String generateShareKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 校验创建命令。
     *
     * @param command 创建命令
     */
    private static void validate(CollectionCreateCommand command) {
        if (command == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "请求不能为空");
        }
        if (command.authorId() <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "authorId 不合法");
        }
        if (!StringUtils.hasText(command.name())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "题单名称不能为空");
        }
        if (command.visibility() == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "visibility 不能为空");
        }
        if (command.visibility() == Visibility.UNLISTED
                && StringUtils.hasText(command.shareKey())
                && command.shareKey().length() < 16) {
            throw new BizException(ErrorCode.BAD_REQUEST, "shareKey 不合法");
        }
    }

    /**
     * 校验添加条目命令。
     *
     * @param command 添加命令
     */
    private static void validate(CollectionAddItemCommand command) {
        if (command == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "请求不能为空");
        }
        if (command.collectionId() <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "collectionId 不合法");
        }
        if (command.authorId() <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "authorId 不合法");
        }
        if (command.problemId() <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "problemId 不合法");
        }
        if (command.sortOrder() < 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "sortOrder 不能小于 0");
        }
    }

    /**
     * 校验排序命令。
     *
     * @param command 排序命令
     */
    private static void validate(CollectionReorderCommand command) {
        if (command == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "请求不能为空");
        }
        if (command.collectionId() <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "collectionId 不合法");
        }
        if (command.authorId() <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "authorId 不合法");
        }
        if (command.items() == null || command.items().isEmpty()) {
            throw new BizException(ErrorCode.BAD_REQUEST, "items 不能为空");
        }
        Set<Long> problemIds = new HashSet<>();
        for (CollectionItemData item : command.items()) {
            if (item.problemId() <= 0) {
                throw new BizException(ErrorCode.BAD_REQUEST, "problemId 不合法");
            }
            if (item.sortOrder() < 0) {
                throw new BizException(ErrorCode.BAD_REQUEST, "sortOrder 不能小于 0");
            }
            if (!problemIds.add(item.problemId())) {
                throw new BizException(ErrorCode.BAD_REQUEST, "items 存在重复 problemId");
            }
        }
    }

    /**
     * 校验更新命令。
     *
     * @param command 更新命令
     */
    private static void validate(CollectionUpdateCommand command) {
        if (command == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "请求不能为空");
        }
        if (command.id() <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "id 不合法");
        }
        if (command.authorId() <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "authorId 不合法");
        }
        if (!StringUtils.hasText(command.name())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "题单名称不能为空");
        }
        if (command.visibility() == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "visibility 不能为空");
        }
        if (command.visibility() == Visibility.UNLISTED
                && StringUtils.hasText(command.shareKey())
                && command.shareKey().length() < 16) {
            throw new BizException(ErrorCode.BAD_REQUEST, "shareKey 不合法");
        }
    }

    /**
     * 校验移除条目命令。
     *
     * @param command 命令
     */
    private static void validate(CollectionRemoveItemCommand command) {
        if (command == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "请求不能为空");
        }
        if (command.collectionId() <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "collectionId 不合法");
        }
        if (command.authorId() <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "authorId 不合法");
        }
        if (command.problemId() <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "problemId 不合法");
        }
    }
}

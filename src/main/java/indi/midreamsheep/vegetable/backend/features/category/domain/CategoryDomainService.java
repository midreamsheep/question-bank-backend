package indi.midreamsheep.vegetable.backend.features.category.domain;

import indi.midreamsheep.vegetable.backend.common.error.BizException;
import indi.midreamsheep.vegetable.backend.common.error.ErrorCode;
import indi.midreamsheep.vegetable.backend.features.category.domain.command.CategoryCreateCommand;
import indi.midreamsheep.vegetable.backend.features.category.domain.command.CategoryUpdateCommand;
import indi.midreamsheep.vegetable.backend.features.category.domain.model.CategoryData;
import indi.midreamsheep.vegetable.backend.features.category.domain.port.CategoryRepositoryPort;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 分类领域服务。
 */
public class CategoryDomainService {

    private final CategoryRepositoryPort categoryRepositoryPort;

    /**
     * 构造分类领域服务。
     *
     * @param categoryRepositoryPort 分类仓储端口
     */
    public CategoryDomainService(CategoryRepositoryPort categoryRepositoryPort) {
        this.categoryRepositoryPort = categoryRepositoryPort;
    }

    /**
     * 获取分类列表。
     *
     * @param subject 学科
     * @return 分类列表
     */
    public List<CategoryData> list(String subject) {
        return categoryRepositoryPort.list(subject);
    }

    /**
     * 创建分类。
     *
     * @param command 创建命令
     * @return 分类ID
     */
    public long create(CategoryCreateCommand command) {
        validate(command);
        return categoryRepositoryPort.create(command);
    }

    /**
     * 更新分类。
     *
     * @param command 更新命令
     * @return 更新后的分类
     */
    public CategoryData update(CategoryUpdateCommand command) {
        validate(command);
        CategoryData existing = categoryRepositoryPort.findById(command.id())
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "分类不存在"));
        if (existing.id() != command.id()) {
            throw new BizException(ErrorCode.BAD_REQUEST, "分类ID不匹配");
        }
        return categoryRepositoryPort.update(command);
    }

    /**
     * 校验创建命令。
     *
     * @param command 创建命令
     */
    private static void validate(CategoryCreateCommand command) {
        if (command == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "请求不能为空");
        }
        if (!StringUtils.hasText(command.name())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "分类名称不能为空");
        }
        if (command.sortOrder() < 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "sortOrder 不能小于 0");
        }
        if (command.parentId() != null && command.parentId() <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "parentId 不合法");
        }
    }

    /**
     * 校验更新命令。
     *
     * @param command 更新命令
     */
    private static void validate(CategoryUpdateCommand command) {
        if (command == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "请求不能为空");
        }
        if (command.id() <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "id 不合法");
        }
        if (!StringUtils.hasText(command.name())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "分类名称不能为空");
        }
        if (command.sortOrder() < 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "sortOrder 不能小于 0");
        }
        if (command.parentId() != null && command.parentId() <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "parentId 不合法");
        }
    }
}

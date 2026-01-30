package indi.midreamsheep.vegetable.backend.features.category.domain.port;

import indi.midreamsheep.vegetable.backend.features.category.domain.command.CategoryCreateCommand;
import indi.midreamsheep.vegetable.backend.features.category.domain.command.CategoryUpdateCommand;
import indi.midreamsheep.vegetable.backend.features.category.domain.model.CategoryData;

import java.util.List;
import java.util.Optional;

/**
 * 分类仓储端口。
 */
public interface CategoryRepositoryPort {

    /**
     * 创建分类。
     *
     * @param command 创建命令
     * @return 分类ID
     */
    long create(CategoryCreateCommand command);

    /**
     * 更新分类。
     *
     * @param command 更新命令
     * @return 更新后的分类
     */
    CategoryData update(CategoryUpdateCommand command);

    /**
     * 获取分类。
     *
     * @param id 分类ID
     * @return 分类数据
     */
    Optional<CategoryData> findById(long id);

    /**
     * 获取分类列表。
     *
     * @param subject 学科
     * @return 分类列表
     */
    List<CategoryData> list(String subject);
}

package indi.midreamsheep.vegetable.backend.features.category.data;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import indi.midreamsheep.vegetable.backend.features.category.domain.command.CategoryCreateCommand;
import indi.midreamsheep.vegetable.backend.features.category.domain.command.CategoryUpdateCommand;
import indi.midreamsheep.vegetable.backend.features.category.domain.model.CategoryData;
import indi.midreamsheep.vegetable.backend.features.category.domain.port.CategoryRepositoryPort;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.CategoryEntity;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper.CategoryMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 分类仓储适配器：通过 MyBatis-Plus 完成持久化。
 */
@Component
public class CategoryRepositoryAdapter implements CategoryRepositoryPort {

    private final CategoryMapper categoryMapper;

    /**
     * 构造分类仓储适配器。
     *
     * @param categoryMapper 分类 Mapper
     */
    public CategoryRepositoryAdapter(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public long create(CategoryCreateCommand command) {
        LocalDateTime now = LocalDateTime.now();
        CategoryEntity entity = new CategoryEntity();
        entity.setSubject(command.subject());
        entity.setParentId(command.parentId());
        entity.setName(command.name());
        entity.setDescription(command.description());
        entity.setSortOrder(command.sortOrder());
        entity.setEnabled(command.enabled() ? 1 : 0);
        entity.setDeleted(0);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        categoryMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public CategoryData update(CategoryUpdateCommand command) {
        LocalDateTime now = LocalDateTime.now();
        UpdateWrapper<CategoryEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", command.id())
                .set("subject", command.subject())
                .set("parent_id", command.parentId())
                .set("name", command.name())
                .set("description", command.description())
                .set("sort_order", command.sortOrder())
                .set("enabled", command.enabled() ? 1 : 0)
                .set("updated_at", now);
        categoryMapper.update(null, wrapper);
        return findById(command.id()).orElseThrow(() -> new IllegalStateException("分类更新失败"));
    }

    @Override
    public Optional<CategoryData> findById(long id) {
        QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id)
                .eq("deleted", 0);
        return Optional.ofNullable(categoryMapper.selectOne(wrapper))
                .map(CategoryRepositoryAdapter::toData);
    }

    @Override
    public List<CategoryData> list(String subject) {
        QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0)
                .eq("enabled", 1);
        if (subject != null) {
            wrapper.eq("subject", subject);
        }
        wrapper.orderByAsc("sort_order").orderByAsc("id");
        return categoryMapper.selectList(wrapper).stream()
                .map(CategoryRepositoryAdapter::toData)
                .toList();
    }

    /**
     * 将实体转换为领域模型。
     *
     * @param entity 分类实体
     * @return 分类数据
     */
    private static CategoryData toData(CategoryEntity entity) {
        return new CategoryData(
                entity.getId(),
                entity.getSubject(),
                entity.getParentId(),
                entity.getName(),
                entity.getDescription(),
                entity.getSortOrder() == null ? 0 : entity.getSortOrder(),
                entity.getEnabled() != null && entity.getEnabled() == 1
        );
    }
}

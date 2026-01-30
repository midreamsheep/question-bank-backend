package indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分类表实体（vf_category）。
 */
@Data
@TableName("vf_category")
public class CategoryEntity {

    /**
     * 主键。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 学科：MATH/PHYSICS（可为空）。
     */
    private String subject;

    /**
     * 父分类ID。
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 分类名称。
     */
    private String name;

    /**
     * 描述。
     */
    private String description;

    /**
     * 排序（越小越靠前）。
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 是否启用。
     */
    private Integer enabled;

    /**
     * 创建时间。
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间。
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    /**
     * 软删除标识。
     */
    private Integer deleted;
}

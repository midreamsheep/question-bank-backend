package indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 题单条目实体（vf_collection_item）。
 */
@Data
@TableName("vf_collection_item")
public class CollectionItemEntity {

    /**
     * 主键。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 题单ID。
     */
    @TableField("collection_id")
    private Long collectionId;

    /**
     * 题目ID。
     */
    @TableField("problem_id")
    private Long problemId;

    /**
     * 排序。
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 加入时间。
     */
    @TableField("added_at")
    private LocalDateTime addedAt;

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

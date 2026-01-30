package indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 题单表实体（vf_collection）。
 */
@Data
@TableName("vf_collection")
public class CollectionEntity {

    /**
     * 主键。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 作者用户ID。
     */
    @TableField("author_id")
    private Long authorId;

    /**
     * 题单名称。
     */
    private String name;

    /**
     * 简介。
     */
    private String description;

    /**
     * 可见性：PUBLIC/UNLISTED/PRIVATE。
     */
    private String visibility;

    /**
     * 分享 key。
     */
    @TableField("share_key")
    private String shareKey;

    /**
     * 状态：ACTIVE/DISABLED。
     */
    private String status;

    /**
     * 题目数量。
     */
    @TableField("item_count")
    private Integer itemCount;

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

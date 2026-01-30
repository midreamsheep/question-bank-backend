package indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 题目-标签关联实体（vf_problem_tag）。
 */
@Data
@TableName("vf_problem_tag")
public class ProblemTagEntity {

    /**
     * 主键。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 题目ID。
     */
    @TableField("problem_id")
    private Long problemId;

    /**
     * 标签ID。
     */
    @TableField("tag_id")
    private Long tagId;

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

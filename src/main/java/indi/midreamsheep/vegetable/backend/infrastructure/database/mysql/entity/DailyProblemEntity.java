package indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 每日一题实体（vf_daily_problem）。
 */
@Data
@TableName("vf_daily_problem")
public class DailyProblemEntity {

    /**
     * 主键。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 日期。
     */
    private LocalDate day;

    /**
     * 题目ID。
     */
    @TableField("problem_id")
    private Long problemId;

    /**
     * 状态：PUBLISHED/REVOKED。
     */
    private String status;

    /**
     * 文案。
     */
    private String copywriting;

    /**
     * 操作人ID。
     */
    @TableField("operator_id")
    private Long operatorId;

    /**
     * 发布时间。
     */
    @TableField("published_at")
    private LocalDateTime publishedAt;

    /**
     * 撤回时间。
     */
    @TableField("revoked_at")
    private LocalDateTime revokedAt;

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

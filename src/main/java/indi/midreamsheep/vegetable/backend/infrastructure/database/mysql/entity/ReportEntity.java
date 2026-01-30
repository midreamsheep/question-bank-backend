package indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 举报实体（vf_report）。
 */
@Data
@TableName("vf_report")
public class ReportEntity {

    /**
     * 主键。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 举报人用户ID。
     */
    @TableField("reporter_id")
    private Long reporterId;

    /**
     * 目标类型。
     */
    @TableField("target_type")
    private String targetType;

    /**
     * 目标ID。
     */
    @TableField("target_id")
    private Long targetId;

    /**
     * 举报原因。
     */
    private String reason;

    /**
     * 状态：OPEN/RESOLVED/REJECTED。
     */
    private String status;

    /**
     * 处理人用户ID。
     */
    @TableField("handler_id")
    private Long handlerId;

    /**
     * 处理时间。
     */
    @TableField("handled_at")
    private LocalDateTime handledAt;

    /**
     * 处理备注。
     */
    @TableField("handling_note")
    private String handlingNote;

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


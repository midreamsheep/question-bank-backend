package indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 题目评论实体（vf_problem_comment）。
 */
@Data
@TableName("vf_problem_comment")
public class ProblemCommentEntity {

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
     * 评论用户ID。
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 父评论ID（楼中楼）。
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 回复的评论ID（可选）。
     */
    @TableField("reply_to_comment_id")
    private Long replyToCommentId;

    /**
     * 评论内容。
     */
    private String content;

    /**
     * 点赞数。
     */
    @TableField("like_count")
    private Long likeCount;

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

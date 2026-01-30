package indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 题目表实体（vf_problem）。
 */
@Data
@TableName("vf_problem")
public class ProblemEntity {

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
     * 标题。
     */
    private String title;

    /**
     * 学科（MATH/PHYSICS）。
     */
    private String subject;

    /**
     * 难度（1-5）。
     */
    private Integer difficulty;

    /**
     * 来源类型。
     */
    @TableField("source_type")
    private String sourceType;

    /**
     * 来源说明。
     */
    @TableField("source_text")
    private String sourceText;

    /**
     * 是否原创。
     */
    @TableField("is_original")
    private Integer isOriginal;

    /**
     * 题干格式：MARKDOWN/LATEX。
     */
    @TableField("statement_format")
    private String statementFormat;

    /**
     * 题干内容。
     */
    @TableField("statement_content")
    private String statementContent;

    /**
     * 解答格式：MARKDOWN/LATEX。
     */
    @TableField("solution_format")
    private String solutionFormat;

    /**
     * 解答内容。
     */
    @TableField("solution_content")
    private String solutionContent;

    /**
     * 可见性：PUBLIC/UNLISTED/PRIVATE。
     */
    private String visibility;

    /**
     * 仅链接访问凭证。
     */
    @TableField("share_key")
    private String shareKey;

    /**
     * 状态：DRAFT/PUBLISHED/DISABLED。
     */
    private String status;

    /**
     * 发布时间（首次发布）。
     */
    @TableField("published_at")
    private LocalDateTime publishedAt;

    /**
     * 内容更新时间（业务口径）。
     */
    @TableField("last_modified_at")
    private LocalDateTime lastModifiedAt;

    /**
     * 浏览数。
     */
    @TableField("view_count")
    private Long viewCount;

    /**
     * 收藏数。
     */
    @TableField("favorite_count")
    private Long favoriteCount;

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

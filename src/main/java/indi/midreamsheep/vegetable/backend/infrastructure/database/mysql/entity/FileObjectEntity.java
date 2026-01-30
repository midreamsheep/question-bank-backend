package indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件对象实体（vf_file_object）。
 */
@Data
@TableName("vf_file_object")
public class FileObjectEntity {

    /**
     * 主键。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 分享 key（永久链接凭证）。
     */
    @TableField("share_key")
    private String shareKey;

    /**
     * 对象存储 key。
     */
    @TableField("object_key")
    private String objectKey;

    /**
     * 原始文件名。
     */
    @TableField("original_filename")
    private String originalFilename;

    /**
     * MIME 类型。
     */
    @TableField("content_type")
    private String contentType;

    /**
     * 文件大小（字节）。
     */
    private Long size;

    /**
     * 上传者用户ID（可为空）。
     */
    @TableField("uploader_id")
    private Long uploaderId;

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
     * 软删除标记。
     */
    private Integer deleted;
}

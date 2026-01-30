package indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户表实体（vf_user）。
 */
@Data
@TableName("vf_user")
public class UserEntity {

    /**
     * 主键。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户名。
     */
    private String username;

    /**
     * 密码哈希。
     */
    @TableField("password_hash")
    private String passwordHash;

    /**
     * 昵称。
     */
    private String nickname;

    /**
     * 头像文件ID。
     */
    @TableField("avatar_file_id")
    private Long avatarFileId;

    /**
     * 状态：ACTIVE/DISABLED。
     */
    private String status;

    /**
     * 最近登录时间。
     */
    @TableField("last_login_at")
    private LocalDateTime lastLoginAt;

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

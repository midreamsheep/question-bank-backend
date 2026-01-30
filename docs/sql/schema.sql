CREATE DATABASE IF NOT EXISTS vegetable_forum
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE vegetable_forum;

-- 注意：请确保你的后端配置 `spring.datasource.url` 连接的库名与这里一致；
-- 否则你执行了建表/导入数据，但应用实际连的是另一个库，会出现“登录失败/查不到数据”等问题。

CREATE TABLE IF NOT EXISTS vf_user (
  id BIGINT NOT NULL PRIMARY KEY,
  username VARCHAR(64) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  nickname VARCHAR(64),
  avatar_file_id BIGINT,
  status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
  last_login_at DATETIME,
  created_at DATETIME,
  updated_at DATETIME,
  deleted TINYINT DEFAULT 0,
  UNIQUE KEY uk_user_username (username),
  KEY idx_user_status (status),
  KEY idx_user_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 文件对象（MinIO 元数据）
CREATE TABLE IF NOT EXISTS vf_file_object (
  id BIGINT NOT NULL PRIMARY KEY,
  share_key CHAR(32) NOT NULL,
  object_key VARCHAR(512) NOT NULL,
  original_filename VARCHAR(256),
  content_type VARCHAR(128),
  size BIGINT NOT NULL DEFAULT 0,
  uploader_id BIGINT,
  created_at DATETIME,
  updated_at DATETIME,
  deleted TINYINT DEFAULT 0,
  UNIQUE KEY uk_file_share_key (share_key),
  UNIQUE KEY uk_file_object_key (object_key),
  KEY idx_file_uploader (uploader_id),
  KEY idx_file_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS vf_role (
  id BIGINT NOT NULL PRIMARY KEY,
  code VARCHAR(32) NOT NULL,
  name VARCHAR(64) NOT NULL,
  created_at DATETIME,
  updated_at DATETIME,
  deleted TINYINT DEFAULT 0,
  UNIQUE KEY uk_role_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS vf_user_role (
  id BIGINT NOT NULL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  created_at DATETIME,
  updated_at DATETIME,
  deleted TINYINT DEFAULT 0,
  UNIQUE KEY uk_user_role (user_id, role_id),
  KEY idx_user_role_user (user_id),
  KEY idx_user_role_role (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS vf_problem (
  id BIGINT NOT NULL PRIMARY KEY,
  author_id BIGINT NOT NULL,
  title VARCHAR(256) NOT NULL,
  subject VARCHAR(64) NOT NULL,
  difficulty TINYINT NOT NULL,
  source_type VARCHAR(32),
  source_text VARCHAR(512),
  is_original TINYINT DEFAULT 0,
  statement_format VARCHAR(16) NOT NULL,
  statement_content MEDIUMTEXT NOT NULL,
  solution_format VARCHAR(16),
  solution_content MEDIUMTEXT,
  visibility VARCHAR(16) NOT NULL,
  share_key CHAR(32),
  status VARCHAR(16) NOT NULL,
  published_at DATETIME,
  last_modified_at DATETIME,
  view_count BIGINT DEFAULT 0,
  favorite_count BIGINT DEFAULT 0,
  like_count BIGINT DEFAULT 0,
  created_at DATETIME,
  updated_at DATETIME,
  deleted TINYINT DEFAULT 0,
  KEY idx_problem_author (author_id),
  KEY idx_problem_subject (subject),
  KEY idx_problem_status_visibility (status, visibility),
  KEY idx_problem_published_at (published_at),
  UNIQUE KEY uk_problem_share_key (share_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS vf_problem_category (
  id BIGINT NOT NULL PRIMARY KEY,
  problem_id BIGINT NOT NULL,
  category_id BIGINT NOT NULL,
  created_at DATETIME,
  updated_at DATETIME,
  deleted TINYINT DEFAULT 0,
  KEY idx_problem_category_problem (problem_id),
  KEY idx_problem_category_category (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS vf_problem_type_rel (
  id BIGINT NOT NULL PRIMARY KEY,
  problem_id BIGINT NOT NULL,
  type_id BIGINT NOT NULL,
  created_at DATETIME,
  updated_at DATETIME,
  deleted TINYINT DEFAULT 0,
  KEY idx_problem_type_problem (problem_id),
  KEY idx_problem_type_type (type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS vf_problem_tag (
  id BIGINT NOT NULL PRIMARY KEY,
  problem_id BIGINT NOT NULL,
  tag_id BIGINT NOT NULL,
  created_at DATETIME,
  updated_at DATETIME,
  deleted TINYINT DEFAULT 0,
  KEY idx_problem_tag_problem (problem_id),
  KEY idx_problem_tag_tag (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS vf_category (
  id BIGINT NOT NULL PRIMARY KEY,
  subject VARCHAR(64),
  parent_id BIGINT,
  name VARCHAR(128) NOT NULL,
  description VARCHAR(512),
  sort_order INT DEFAULT 0,
  enabled TINYINT DEFAULT 1,
  created_at DATETIME,
  updated_at DATETIME,
  deleted TINYINT DEFAULT 0,
  KEY idx_category_subject (subject),
  KEY idx_category_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS vf_problem_type (
  id BIGINT NOT NULL PRIMARY KEY,
  subject VARCHAR(64),
  name VARCHAR(128) NOT NULL,
  description VARCHAR(512),
  sort_order INT DEFAULT 0,
  enabled TINYINT DEFAULT 1,
  created_at DATETIME,
  updated_at DATETIME,
  deleted TINYINT DEFAULT 0,
  KEY idx_problem_type_subject (subject)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS vf_tag (
  id BIGINT NOT NULL PRIMARY KEY,
  subject VARCHAR(64),
  name VARCHAR(64) NOT NULL,
  created_at DATETIME,
  updated_at DATETIME,
  deleted TINYINT DEFAULT 0,
  KEY idx_tag_subject (subject)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS vf_collection (
  id BIGINT NOT NULL PRIMARY KEY,
  author_id BIGINT NOT NULL,
  name VARCHAR(128) NOT NULL,
  description VARCHAR(1024),
  visibility VARCHAR(16) NOT NULL,
  share_key CHAR(32),
  status VARCHAR(16) NOT NULL,
  item_count INT DEFAULT 0,
  created_at DATETIME,
  updated_at DATETIME,
  deleted TINYINT DEFAULT 0,
  KEY idx_collection_author (author_id),
  UNIQUE KEY uk_collection_share_key (share_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS vf_collection_item (
  id BIGINT NOT NULL PRIMARY KEY,
  collection_id BIGINT NOT NULL,
  problem_id BIGINT NOT NULL,
  sort_order INT DEFAULT 0,
  added_at DATETIME,
  created_at DATETIME,
  updated_at DATETIME,
  deleted TINYINT DEFAULT 0,
  KEY idx_collection_item_collection (collection_id),
  KEY idx_collection_item_problem (problem_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS vf_daily_problem (
  id BIGINT NOT NULL PRIMARY KEY,
  day DATE NOT NULL,
  problem_id BIGINT NOT NULL,
  status VARCHAR(16) NOT NULL,
  copywriting VARCHAR(1024),
  operator_id BIGINT NOT NULL,
  published_at DATETIME,
  revoked_at DATETIME,
  created_at DATETIME,
  updated_at DATETIME,
  deleted TINYINT DEFAULT 0,
  UNIQUE KEY uk_daily_problem_day_problem (day, problem_id),
  KEY idx_daily_problem_problem (problem_id),
  KEY idx_daily_problem_day (day),
  KEY idx_daily_problem_published_at (published_at),
  KEY idx_daily_problem_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户收藏（题目/题单）
CREATE TABLE IF NOT EXISTS vf_user_favorite_problem (
  id BIGINT NOT NULL PRIMARY KEY,
  user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  problem_id BIGINT UNSIGNED NOT NULL COMMENT '题目ID',
  created_at DATETIME,
  updated_at DATETIME,
  deleted TINYINT DEFAULT 0,
  UNIQUE KEY uk_user_fav_problem (user_id, problem_id),
  KEY idx_user_fav_problem_user (user_id),
  KEY idx_user_fav_problem_problem (problem_id),
  KEY idx_user_fav_problem_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS vf_user_favorite_collection (
  id BIGINT NOT NULL PRIMARY KEY,
  user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  collection_id BIGINT UNSIGNED NOT NULL COMMENT '题单ID',
  created_at DATETIME,
  updated_at DATETIME,
  deleted TINYINT DEFAULT 0,
  UNIQUE KEY uk_user_fav_collection (user_id, collection_id),
  KEY idx_user_fav_collection_user (user_id),
  KEY idx_user_fav_collection_collection (collection_id),
  KEY idx_user_fav_collection_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户点赞（题目）
CREATE TABLE IF NOT EXISTS vf_user_like_problem (
  id BIGINT NOT NULL PRIMARY KEY,
  user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  problem_id BIGINT UNSIGNED NOT NULL COMMENT '题目ID',
  created_at DATETIME,
  updated_at DATETIME,
  deleted TINYINT DEFAULT 0,
  UNIQUE KEY uk_user_like_problem (user_id, problem_id),
  KEY idx_user_like_problem_user (user_id),
  KEY idx_user_like_problem_problem (problem_id),
  KEY idx_user_like_problem_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 题目评论（扁平结构，MVP/V1.1）
CREATE TABLE IF NOT EXISTS vf_problem_comment (
  id BIGINT NOT NULL PRIMARY KEY,
  problem_id BIGINT UNSIGNED NOT NULL COMMENT '题目ID',
  user_id BIGINT UNSIGNED NOT NULL COMMENT '评论用户ID',
  parent_id BIGINT UNSIGNED NULL COMMENT '父评论ID（楼中楼）',
  reply_to_comment_id BIGINT UNSIGNED NULL COMMENT '回复的评论ID（可选）',
  content VARCHAR(2000) NOT NULL COMMENT '评论内容',
  like_count BIGINT DEFAULT 0 COMMENT '点赞数',
  created_at DATETIME,
  updated_at DATETIME,
  deleted TINYINT DEFAULT 0,
  KEY idx_problem_comment_problem (problem_id),
  KEY idx_problem_comment_parent (parent_id),
  KEY idx_problem_comment_user (user_id),
  KEY idx_problem_comment_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户点赞（评论）
CREATE TABLE IF NOT EXISTS vf_user_like_comment (
  id BIGINT NOT NULL PRIMARY KEY,
  user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  comment_id BIGINT UNSIGNED NOT NULL COMMENT '评论ID',
  created_at DATETIME,
  updated_at DATETIME,
  deleted TINYINT DEFAULT 0,
  UNIQUE KEY uk_user_like_comment (user_id, comment_id),
  KEY idx_user_like_comment_user (user_id),
  KEY idx_user_like_comment_comment (comment_id),
  KEY idx_user_like_comment_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 举报（题目/评论等）
CREATE TABLE IF NOT EXISTS vf_report (
  id BIGINT NOT NULL PRIMARY KEY,
  reporter_id BIGINT UNSIGNED NOT NULL COMMENT '举报人用户ID',
  target_type VARCHAR(32) NOT NULL COMMENT '目标类型：PROBLEM/COMMENT',
  target_id BIGINT UNSIGNED NOT NULL COMMENT '目标ID',
  reason VARCHAR(1024) NOT NULL COMMENT '举报原因',
  status VARCHAR(16) NOT NULL COMMENT '状态：OPEN/RESOLVED/REJECTED',
  handler_id BIGINT UNSIGNED NULL COMMENT '处理人用户ID（管理员）',
  handled_at DATETIME NULL COMMENT '处理时间',
  handling_note VARCHAR(1024) NULL COMMENT '处理备注',
  created_at DATETIME,
  updated_at DATETIME,
  deleted TINYINT DEFAULT 0,
  KEY idx_report_target (target_type, target_id),
  KEY idx_report_status (status),
  KEY idx_report_reporter (reporter_id),
  KEY idx_report_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

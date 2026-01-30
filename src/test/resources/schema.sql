CREATE TABLE IF NOT EXISTS vf_problem (
  id BIGINT PRIMARY KEY,
  author_id BIGINT NOT NULL,
  title VARCHAR(256) NOT NULL,
  subject VARCHAR(64) NOT NULL,
  difficulty TINYINT NOT NULL,
  source_type VARCHAR(32),
  source_text VARCHAR(512),
  is_original TINYINT DEFAULT 0,
  statement_format VARCHAR(16) NOT NULL,
  statement_content CLOB NOT NULL,
  solution_format VARCHAR(16),
  solution_content CLOB,
  visibility VARCHAR(16) NOT NULL,
  share_key CHAR(32),
  status VARCHAR(16) NOT NULL,
  published_at TIMESTAMP NULL,
  last_modified_at TIMESTAMP NULL,
  view_count BIGINT DEFAULT 0,
  favorite_count BIGINT DEFAULT 0,
  like_count BIGINT DEFAULT 0,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  CONSTRAINT uk_problem_share_key UNIQUE (share_key)
);

CREATE TABLE IF NOT EXISTS vf_user (
  id BIGINT PRIMARY KEY,
  username VARCHAR(64) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  nickname VARCHAR(64),
  avatar_file_id BIGINT,
  status VARCHAR(16) NOT NULL,
  last_login_at TIMESTAMP,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  CONSTRAINT uk_user_username UNIQUE (username)
);

CREATE TABLE IF NOT EXISTS vf_file_object (
  id BIGINT PRIMARY KEY,
  share_key CHAR(32) NOT NULL,
  object_key VARCHAR(512) NOT NULL,
  original_filename VARCHAR(256),
  content_type VARCHAR(128),
  size BIGINT DEFAULT 0,
  uploader_id BIGINT,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  CONSTRAINT uk_file_share_key UNIQUE (share_key),
  CONSTRAINT uk_file_object_key UNIQUE (object_key)
);

CREATE TABLE IF NOT EXISTS vf_role (
  id BIGINT PRIMARY KEY,
  code VARCHAR(32) NOT NULL,
  name VARCHAR(64) NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  CONSTRAINT uk_role_code UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS vf_user_role (
  id BIGINT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  CONSTRAINT uk_user_role UNIQUE (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS vf_problem_category (
  id BIGINT PRIMARY KEY,
  problem_id BIGINT NOT NULL,
  category_id BIGINT NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS vf_problem_type_rel (
  id BIGINT PRIMARY KEY,
  problem_id BIGINT NOT NULL,
  type_id BIGINT NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS vf_problem_tag (
  id BIGINT PRIMARY KEY,
  problem_id BIGINT NOT NULL,
  tag_id BIGINT NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS vf_category (
  id BIGINT PRIMARY KEY,
  subject VARCHAR(64),
  parent_id BIGINT,
  name VARCHAR(128) NOT NULL,
  description VARCHAR(512),
  sort_order INT DEFAULT 0,
  enabled TINYINT DEFAULT 1,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS vf_problem_type (
  id BIGINT PRIMARY KEY,
  subject VARCHAR(64),
  name VARCHAR(128) NOT NULL,
  description VARCHAR(512),
  sort_order INT DEFAULT 0,
  enabled TINYINT DEFAULT 1,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS vf_tag (
  id BIGINT PRIMARY KEY,
  subject VARCHAR(64),
  name VARCHAR(64) NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS vf_collection (
  id BIGINT PRIMARY KEY,
  author_id BIGINT NOT NULL,
  name VARCHAR(128) NOT NULL,
  description VARCHAR(1024),
  visibility VARCHAR(16) NOT NULL,
  share_key CHAR(32),
  status VARCHAR(16) NOT NULL,
  item_count INT DEFAULT 0,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  CONSTRAINT uk_collection_share_key UNIQUE (share_key)
);

CREATE TABLE IF NOT EXISTS vf_collection_item (
  id BIGINT PRIMARY KEY,
  collection_id BIGINT NOT NULL,
  problem_id BIGINT NOT NULL,
  sort_order INT DEFAULT 0,
  added_at TIMESTAMP,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS vf_daily_problem (
  id BIGINT PRIMARY KEY,
  day DATE NOT NULL,
  problem_id BIGINT NOT NULL,
  status VARCHAR(16) NOT NULL,
  copywriting VARCHAR(1024),
  operator_id BIGINT NOT NULL,
  published_at TIMESTAMP,
  revoked_at TIMESTAMP,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  CONSTRAINT uk_daily_problem_day_problem UNIQUE (day, problem_id)
);

CREATE TABLE IF NOT EXISTS vf_user_favorite_problem (
  id BIGINT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  problem_id BIGINT NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  CONSTRAINT uk_user_fav_problem UNIQUE (user_id, problem_id)
);

CREATE TABLE IF NOT EXISTS vf_user_favorite_collection (
  id BIGINT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  collection_id BIGINT NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  CONSTRAINT uk_user_fav_collection UNIQUE (user_id, collection_id)
);

CREATE TABLE IF NOT EXISTS vf_user_like_problem (
  id BIGINT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  problem_id BIGINT NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  CONSTRAINT uk_user_like_problem UNIQUE (user_id, problem_id)
);

CREATE TABLE IF NOT EXISTS vf_problem_comment (
  id BIGINT PRIMARY KEY,
  problem_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  parent_id BIGINT,
  reply_to_comment_id BIGINT,
  content VARCHAR(2000) NOT NULL,
  like_count BIGINT DEFAULT 0,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS vf_user_like_comment (
  id BIGINT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  comment_id BIGINT NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  CONSTRAINT uk_user_like_comment UNIQUE (user_id, comment_id)
);

CREATE TABLE IF NOT EXISTS vf_report (
  id BIGINT PRIMARY KEY,
  reporter_id BIGINT NOT NULL,
  target_type VARCHAR(32) NOT NULL,
  target_id BIGINT NOT NULL,
  reason VARCHAR(1024) NOT NULL,
  status VARCHAR(16) NOT NULL,
  handler_id BIGINT,
  handled_at TIMESTAMP,
  handling_note VARCHAR(1024),
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  deleted TINYINT DEFAULT 0
);

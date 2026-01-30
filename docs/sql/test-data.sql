USE vegetable_forum;

-- 说明：本文件设计为“可重复执行”（幂等）。
-- 如果库中已存在相同主键/唯一键数据，会进行更新以避免 Duplicate entry。
-- 注意：请确保你的后端配置 `spring.datasource.url` 连接的库名与这里一致。
--
-- 可用于本地联调的测试账号（明文密码）：
-- - 普通用户：username=demo, password=demo（USER）
-- - 管理员：username=admin, password=demo（ADMIN + USER）
-- - 禁用用户：username=disabled_user, password=demo（DISABLED）

-- =========================
-- 用户与角色
-- =========================
-- 说明：以下用户密码统一为 demo（BCrypt hash 与测试环境一致）。
INSERT INTO vf_user (id, username, password_hash, nickname, avatar_file_id, status, last_login_at, created_at, updated_at, deleted)
VALUES
  (90001, 'demo', '$2a$10$HhdNF/oJWYOs9Chmxg2ALeAl/xEN6ktzDn.JcHc7hZ6DZmWu1urEm', 'Demo', NULL, 'ACTIVE', NULL, NOW(), NOW(), 0),
  (90002, 'admin', '$2a$10$HhdNF/oJWYOs9Chmxg2ALeAl/xEN6ktzDn.JcHc7hZ6DZmWu1urEm', 'Admin', NULL, 'ACTIVE', NULL, NOW(), NOW(), 0),
  (90003, 'disabled_user', '$2a$10$HhdNF/oJWYOs9Chmxg2ALeAl/xEN6ktzDn.JcHc7hZ6DZmWu1urEm', 'Disabled', NULL, 'DISABLED', NULL, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE
  username = VALUES(username),
  password_hash = VALUES(password_hash),
  nickname = VALUES(nickname),
  avatar_file_id = VALUES(avatar_file_id),
  status = VALUES(status),
  last_login_at = VALUES(last_login_at),
  updated_at = NOW(),
  deleted = VALUES(deleted);

INSERT INTO vf_role (id, code, name, created_at, updated_at, deleted)
VALUES
  (90001, 'USER', '普通用户', NOW(), NOW(), 0),
  (90002, 'ADMIN', '管理员', NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE
  code = VALUES(code),
  name = VALUES(name),
  updated_at = NOW(),
  deleted = VALUES(deleted);

INSERT INTO vf_user_role (id, user_id, role_id, created_at, updated_at, deleted)
VALUES
  (91001, 90001, 90001, NOW(), NOW(), 0),
  (91002, 90002, 90002, NOW(), NOW(), 0),
  (91003, 90002, 90001, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE
  user_id = VALUES(user_id),
  role_id = VALUES(role_id),
  updated_at = NOW(),
  deleted = VALUES(deleted);

-- =========================
-- 分类 / 题型 / 标签
-- =========================
INSERT INTO vf_category (id, subject, parent_id, name, description, sort_order, enabled, created_at, updated_at, deleted)
VALUES
  (10001, 'MATH', NULL, 'Algebra', 'Algebra basics', 1, 1, NOW(), NOW(), 0),
  (10002, 'PHYSICS', NULL, 'Mechanics', 'Mechanics basics', 1, 1, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE
  subject = VALUES(subject),
  parent_id = VALUES(parent_id),
  name = VALUES(name),
  description = VALUES(description),
  sort_order = VALUES(sort_order),
  enabled = VALUES(enabled),
  updated_at = NOW(),
  deleted = VALUES(deleted);

INSERT INTO vf_problem_type (id, subject, name, description, sort_order, enabled, created_at, updated_at, deleted)
VALUES
  (11001, 'MATH', 'SingleChoice', 'Single choice question', 1, 1, NOW(), NOW(), 0),
  (11002, 'PHYSICS', 'Proof', 'Proof question', 1, 1, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE
  subject = VALUES(subject),
  name = VALUES(name),
  description = VALUES(description),
  sort_order = VALUES(sort_order),
  enabled = VALUES(enabled),
  updated_at = NOW(),
  deleted = VALUES(deleted);

INSERT INTO vf_tag (id, subject, name, created_at, updated_at, deleted)
VALUES
  (12001, 'MATH', 'Linear', NOW(), NOW(), 0),
  (12002, 'PHYSICS', 'Newton', NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE
  subject = VALUES(subject),
  name = VALUES(name),
  updated_at = NOW(),
  deleted = VALUES(deleted);

-- =========================
-- 题目 / 题目关联 / 审核
-- =========================
INSERT INTO vf_problem (
  id, author_id, title, subject, difficulty, source_type, source_text, is_original,
  statement_format, statement_content, solution_format, solution_content,
  visibility, share_key, status, published_at, last_modified_at, view_count, favorite_count,
  like_count, created_at, updated_at, deleted
)
VALUES
  (
    20001, 90001, 'Solve linear equation', 'MATH', 2, 'BOOK', 'Textbook A', 1,
    'MARKDOWN', 'Solve x + 3 = 7.', 'MARKDOWN', 'x = 4.',
    'PUBLIC', NULL, 'PUBLISHED', NOW(), NOW(), 10, 2,
    1, NOW(), NOW(), 0
  ),
  (
    20002, 90001, 'Free fall basics', 'PHYSICS', 3, 'EXAM', 'Mock Exam', 0,
    'MARKDOWN', 'Describe free fall.', NULL, NULL,
    'PRIVATE', NULL, 'DRAFT', NULL, NOW(), 0, 0,
    0, NOW(), NOW(), 0
  ),
  (
    20004, 90001, 'Unlisted sample', 'MATH', 3, NULL, NULL, 1,
    'LATEX', '\\\\text{Unlisted statement}', 'LATEX', '\\\\text{Unlisted solution}',
    'UNLISTED', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 'PUBLISHED', NOW(), NOW(), 0, 0,
    0, NOW(), NOW(), 0
  ),
  (
    20003, 90001, 'Disabled sample', 'MATH', 4, NULL, NULL, 1,
    'MARKDOWN', 'This problem is disabled.', 'MARKDOWN', 'TBD',
    'PUBLIC', NULL, 'DISABLED', NOW(), NOW(), 0, 0,
    0, NOW(), NOW(), 0
  )
ON DUPLICATE KEY UPDATE
  author_id = VALUES(author_id),
  title = VALUES(title),
  subject = VALUES(subject),
  difficulty = VALUES(difficulty),
  source_type = VALUES(source_type),
  source_text = VALUES(source_text),
  is_original = VALUES(is_original),
  statement_format = VALUES(statement_format),
  statement_content = VALUES(statement_content),
  solution_format = VALUES(solution_format),
  solution_content = VALUES(solution_content),
  visibility = VALUES(visibility),
  share_key = VALUES(share_key),
  status = VALUES(status),
  published_at = VALUES(published_at),
  last_modified_at = VALUES(last_modified_at),
  view_count = VALUES(view_count),
  favorite_count = VALUES(favorite_count),
  like_count = VALUES(like_count),
  updated_at = NOW(),
  deleted = VALUES(deleted);

INSERT INTO vf_problem_category (id, problem_id, category_id, created_at, updated_at, deleted)
VALUES
  (30001, 20001, 10001, NOW(), NOW(), 0),
  (30002, 20002, 10002, NOW(), NOW(), 0),
  (30003, 20003, 10001, NOW(), NOW(), 0),
  (30004, 20004, 10001, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE
  problem_id = VALUES(problem_id),
  category_id = VALUES(category_id),
  updated_at = NOW(),
  deleted = VALUES(deleted);

INSERT INTO vf_problem_type_rel (id, problem_id, type_id, created_at, updated_at, deleted)
VALUES
  (31001, 20001, 11001, NOW(), NOW(), 0),
  (31002, 20002, 11002, NOW(), NOW(), 0),
  (31003, 20003, 11001, NOW(), NOW(), 0),
  (31004, 20004, 11001, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE
  problem_id = VALUES(problem_id),
  type_id = VALUES(type_id),
  updated_at = NOW(),
  deleted = VALUES(deleted);

INSERT INTO vf_problem_tag (id, problem_id, tag_id, created_at, updated_at, deleted)
VALUES
  (32001, 20001, 12001, NOW(), NOW(), 0),
  (32002, 20002, 12002, NOW(), NOW(), 0),
  (32003, 20003, 12001, NOW(), NOW(), 0),
  (32004, 20004, 12001, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE
  problem_id = VALUES(problem_id),
  tag_id = VALUES(tag_id),
  updated_at = NOW(),
  deleted = VALUES(deleted);

-- =========================
-- 题单 / 题单条目
-- =========================
INSERT INTO vf_collection (
  id, author_id, name, description, visibility, share_key, status, item_count,
  created_at, updated_at, deleted
)
VALUES
  (
    40001, 90001, 'Starter Pack', 'Intro problems', 'PUBLIC',
    NULL, 'ACTIVE', 2, NOW(), NOW(), 0
  ),
  (
    40002, 90001, 'Unlisted Pack', 'Shared by link', 'UNLISTED',
    'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', 'ACTIVE', 1, NOW(), NOW(), 0
  )
ON DUPLICATE KEY UPDATE
  author_id = VALUES(author_id),
  name = VALUES(name),
  description = VALUES(description),
  visibility = VALUES(visibility),
  share_key = VALUES(share_key),
  status = VALUES(status),
  item_count = VALUES(item_count),
  updated_at = NOW(),
  deleted = VALUES(deleted);

INSERT INTO vf_collection_item (
  id, collection_id, problem_id, sort_order, added_at, created_at, updated_at, deleted
)
VALUES
  (41001, 40001, 20001, 10, NOW(), NOW(), NOW(), 0),
  (41002, 40001, 20004, 20, NOW(), NOW(), NOW(), 0),
  (41003, 40002, 20001, 10, NOW(), NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE
  collection_id = VALUES(collection_id),
  problem_id = VALUES(problem_id),
  sort_order = VALUES(sort_order),
  added_at = VALUES(added_at),
  updated_at = NOW(),
  deleted = VALUES(deleted);

-- =========================
-- 每日一题
-- =========================
INSERT INTO vf_daily_problem (
  id, day, problem_id, status, copywriting, operator_id,
  published_at, revoked_at, created_at, updated_at, deleted
)
VALUES
  (42001, '2026-01-28', 20001, 'PUBLISHED', 'Daily practice', 90002,
   NOW(), NULL, NOW(), NOW(), 0),
  (42002, '2026-01-28', 20002, 'PUBLISHED', 'Daily practice (2)', 90002,
   NOW(), NULL, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE
  problem_id = VALUES(problem_id),
  status = VALUES(status),
  copywriting = VALUES(copywriting),
  operator_id = VALUES(operator_id),
  published_at = VALUES(published_at),
  revoked_at = VALUES(revoked_at),
  updated_at = NOW(),
  deleted = VALUES(deleted);

-- =========================
-- 收藏 / 点赞
-- =========================
INSERT INTO vf_user_favorite_problem (id, user_id, problem_id, created_at, updated_at, deleted)
VALUES
  (51001, 90001, 20001, NOW(), NOW(), 0),
  (51002, 90002, 20001, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE
  user_id = VALUES(user_id),
  problem_id = VALUES(problem_id),
  updated_at = NOW(),
  deleted = VALUES(deleted);

INSERT INTO vf_user_like_problem (id, user_id, problem_id, created_at, updated_at, deleted)
VALUES
  (52001, 90002, 20001, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE
  user_id = VALUES(user_id),
  problem_id = VALUES(problem_id),
  updated_at = NOW(),
  deleted = VALUES(deleted);

INSERT INTO vf_user_favorite_collection (id, user_id, collection_id, created_at, updated_at, deleted)
VALUES
  (53001, 90002, 40001, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE
  user_id = VALUES(user_id),
  collection_id = VALUES(collection_id),
  updated_at = NOW(),
  deleted = VALUES(deleted);

-- =========================
-- 评论 / 评论点赞
-- =========================
INSERT INTO vf_problem_comment (
  id, problem_id, user_id, parent_id, reply_to_comment_id, content, like_count,
  created_at, updated_at, deleted
)
VALUES
  (60001, 20001, 90002, NULL, NULL, 'Nice problem!', 0, NOW(), NOW(), 0),
  (60002, 20001, 90001, 60001, 60001, 'Thanks!', 1, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE
  problem_id = VALUES(problem_id),
  user_id = VALUES(user_id),
  parent_id = VALUES(parent_id),
  reply_to_comment_id = VALUES(reply_to_comment_id),
  content = VALUES(content),
  like_count = VALUES(like_count),
  updated_at = NOW(),
  deleted = VALUES(deleted);

INSERT INTO vf_user_like_comment (id, user_id, comment_id, created_at, updated_at, deleted)
VALUES
  (61001, 90002, 60002, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE
  user_id = VALUES(user_id),
  comment_id = VALUES(comment_id),
  updated_at = NOW(),
  deleted = VALUES(deleted);

-- =========================
-- 举报（题目 / 评论）
-- =========================
INSERT INTO vf_report (
  id, reporter_id, target_type, target_id, reason, status,
  handler_id, handled_at, handling_note, created_at, updated_at, deleted
)
VALUES
  (70001, 90001, 'COMMENT', 60001, 'Spam?', 'OPEN', NULL, NULL, NULL, NOW(), NOW(), 0),
  (70002, 90001, 'PROBLEM', 20004, 'Inappropriate content', 'RESOLVED', 90002, NOW(), 'Reviewed and ok', NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE
  reporter_id = VALUES(reporter_id),
  target_type = VALUES(target_type),
  target_id = VALUES(target_id),
  reason = VALUES(reason),
  status = VALUES(status),
  handler_id = VALUES(handler_id),
  handled_at = VALUES(handled_at),
  handling_note = VALUES(handling_note),
  updated_at = NOW(),
  deleted = VALUES(deleted);

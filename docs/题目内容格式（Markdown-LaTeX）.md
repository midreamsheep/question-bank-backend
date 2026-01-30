# 题目内容格式（Markdown / LaTeX）

本项目支持题目提交时选择书写格式，以满足“图文 + 公式”的题目表达需求。

## 1. 支持的格式

### 1.1 Markdown

- 适用于：普通文本、分段说明、列表、代码块、插图等
- 典型用法：题干描述 + 图片 + 分步解答
- 公式：可在 Markdown 中嵌入 LaTeX 片段（是否支持及渲染方式由前端决定）

### 1.2 LaTeX

- 适用于：公式密集、排版要求较强的内容
- 后端存储的是 LaTeX 源码，前端可使用 KaTeX/MathJax 进行渲染

## 2. 数据存储口径（后端）

题干与解答各自包含：

- `format`：`MARKDOWN` / `LATEX`
- `content`：对应格式的文本内容

数据库字段建议见：`docs/数据库表设计.md`

## 3. API 口径（后端）

题目提交时建议分别传入：

- `statementFormat` + `statement`
- `solutionFormat` + `solution`

后端不会自动将 Markdown 与 LaTeX 相互转换，只负责存储与返回格式信息。


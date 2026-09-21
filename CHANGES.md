# CHANGES · 相对上游的改动声明

> 本文件记录本 fork 相对 [rikkahub/rikkahub](https://github.com/rikkahub/rikkahub) 的改动。
> **合规依据**：AGPL-3.0 §5a —— 修改版本应显著标注改动内容与日期。

## 基线

| 项目 | 值 |
| --- | --- |
| 上游仓库 | [https://github.com/rikkahub/rikkahub](https://github.com/rikkahub/rikkahub) |
| 基准版本 | **2.5.2（versionCode 187）** |
| 基准 commit | `643191229dcbeb4972e3772986818605633ace92` |
| 上游协议 | **AGPL-3.0** |
| 本 fork 协议 | **AGPL-3.0**（与上游一致） |

## [v2.5.2-plus.1] — 2026-09-20

> 基于上游 `2.5.2`。本版为 fork 首个版本。

### 新增

- **日历删除工具 `calendar_delete`**
  - 之前只有 `calendar_query`（查）和 `calendar_create`（建），现在可以删除建错的日程。
  - 通过事件 ID 删除；ID 取自 `calendar_query` 返回的 `id`，或 `calendar_create` 返回的 `event_id`。
  - 复用已有的「日历」工具开关，不新增用户可见开关。
  - 需要系统日历写权限；删除操作每次都会请求用户确认。
  - 工具调用卡片显示为“删除事件：标题”，与创建事件卡片的显示方式保持一致。

- **助手记忆分组**
  - 支持私有、全局和自定义记忆分组。
  - 支持新建、改名和删除自定义分组。
  - 删除分组前显示受影响的记忆数和成员数。
  - 删除分组时，组内记忆回落到私有范围，不直接删除记忆内容。
  - 保留既有 `useGlobalMemory` 字段，并兼容已有全局记忆数据，无需数据库迁移。

### 修改

- 助手记忆页将原“全局记忆”开关改为“记忆分组”入口，以便选择私有、全局或自定义分组。
- 助手记忆页、助手详情、聊天服务和记忆工具等既有读写入口统一按记忆分组作用域访问数据。
- 未修改既有 `memory_tool` 的名称、参数或输出字段。
- 未修改既有 `calendar_query` 的输出字段：事件 ID 仍为 `id`。

### 包名 / 签名状态

- 当前 Debug 包名：`me.rerere.rikkahub.debug`。
- 当前 Debug 包使用 Debug 签名，可与官方包共存安装；当前未生成正式 Release 包。
- 后续正式 Release 计划使用包名：`me.rerere.rikkahub.plus`，并使用独立签名；该 Release 尚未签名或生成。
- 当前源码 namespace 保持为 `me.rerere.rikkahub`，不影响后续包名迁移安排。

### 借鉴来源声明

- `calendar_delete` 的实现思路参考 [YaeNovin/Rikkahub-Revised](https://github.com/YaeNovin/Rikkahub-Revised)（同为 AGPL-3.0，借鉴合法）。
- **但未照抄**：该 fork 同时把 `calendar_query` 的输出字段 `id` 改成了 `event_id`，属于对既有行为的破坏性改动；本 fork 未采纳。

## [F003] 更新源改造 — 2026-09-21

> 基于上游 `2.5.2` 和本 fork 已有改动；本次正式发布版本为 `2.5.3-work.1`（versionCode 188）。

### 修改

- 更新检查数据源改为本 fork 的 GitHub Release：`ziwa0213-crypto/rikkahub_work`。
- 点击更新卡片改为打开 GitHub 发布页，由用户自行下载并安装 APK；移除应用内下载逻辑和下载弹窗。
- 网络失败、404、限流和 JSON 解析失败不再展示错误卡片，统一静默处理。
- GitHub Release 的 `tag_name` 支持可选 `v` 前缀，比较时仍使用现有 SemVer 规则。

### 发布约定

- Release tag 使用不带 `v` 前缀的 SemVer，且核心版本号必须高于已安装版本，例如 `2.5.3-work.1`。
- GitHub 仓库必须保持公开，否则匿名更新检查会静默为无更新。
- 应用显示名称统一为 `Rikkahub Work`；Debug 与 Release 的包名仍分别为 `me.rerere.rikkahub.debug` 和 `me.rerere.rikkahub.plus`。

## [v2.5.3-work.1] — 2026-09-21

> F004 工具审批模式与 F003 更新源改造的正式发布版本，基于上游 `2.5.2` 和本 fork 已有改动。

### 新增

- 聊天输入栏新增按助手保存的三档工具审批模式：需要询问、部分询问、完全允许。
- 默认模式为“部分询问”，仅高风险改写、删除和执行操作需要确认；工具本身的审批判断在部分模式下仍会保留并使用 OR 叠加。
- 使用 HugeIcons 安全图标区分三档：`ShieldQuestionMark`、`ShieldCheck`、`ShieldOff`。
- 工具风险采用中央 L0-L3 分级；未知工具默认按 L3 处理。

### 有意行为变更

- `calendar_create` 在默认“部分询问”模式下不再二次确认；`ask_user` 始终由 HITL 流程拦截，不进入普通工具执行路径。
- 默认“部分询问”模式下，所有 `mcp__*` 工具以及 workspace 的 read/write/edit 工具按 L3 处理并要求确认；这些工具各自设置页的审批开关不能关闭这一层确认。`workspace_shell` 继续要求确认。
- “完全允许”会跳过应用内审批，包括工作区路径越界护栏，但不绕过 Android 系统权限，例如日历或存储授权。

### 包名 / 签名状态

- Debug 包名：`me.rerere.rikkahub.debug`。
- Release 包名：`me.rerere.rikkahub.plus`，使用独立签名。
- Debug APK 仅用于本地开发，不上传 GitHub。
- Release APK 使用独立签名，GitHub Release 资产名为 `Rikkahub Work-release.apk`。
- 正式版本：`versionName=2.5.3-work.1`，`versionCode=188`。

## 协议声明

本 fork 以 **AGPL-3.0** 授权，原始版权归 RikkaHub 作者所有。
完整的许可证全文见仓库根目录 [LICENSE](./LICENSE)。

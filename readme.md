# 学生选课管理系统（Spring Boot + Spring Data JPA）

该实现基于 Spring Boot + Spring Data JPA 完成，满足题目要求的学生注册、首次登录修改密码、选课/退课、课程与专业管理、账号管理、选课信息查询等功能，默认使用内存 H2 数据库便于在笔记本直接演示，无需额外环境。

## 快速运行

```bash
mvn spring-boot:run
```

启动后可通过 [http://localhost:8080/h2-console](http://localhost:8080/h2-console) 查看内存数据库（JDBC URL: `jdbc:h2:mem:demo`）。

系统预置管理员账号：

- 账号：`admin`
- 密码：`admin123!`（已按 MD5 存储）

## 主要接口

### 学生注册与登录

- `POST /api/students/register`：注册学生，必填姓名、年龄、手机号（唯一）、入学时间、专业 ID；系统自动生成学号与默认密码 `123456`（MD5），返回学生信息。
- `POST /api/students/login`：学号 + 密码登录，返回账号信息（包含是否需要修改密码标记）。
- `POST /api/students/change-password`：首次登录后修改密码，新密码必须包含数字、字母、符号且至少 8 位。

### 学生选课

- `GET /api/students/{studentNo}/courses`：查看已选/可选课程列表及学分汇总。
- `POST /api/students/{studentNo}/courses/select`：提交课程 ID 进行选课。
- `POST /api/students/{studentNo}/courses/drop`：提交课程 ID 退选，学分统计自动更新。

### 管理员功能（需要 `admin` 参数传入管理员账号）

- 课程：`GET/POST /api/admin/courses`、`DELETE /api/admin/courses/{id}`，已有学生选的课程禁止删除。
- 专业：`GET/POST /api/admin/majors`、`DELETE /api/admin/majors/{id}`，有学生关联的专业禁止删除。
- 账号：`POST /api/admin/accounts` 创建管理员/学生账号（密码自动 MD5）。
- 选课查询：`GET /api/admin/enrollments` 查看全部学生选课情况。

## 数据与模型

实体设计涵盖专业（`Major`）、课程（`Course`）、账号（`Account`）、学生（`Student`，含学号生成规则和手机号唯一约束）、选课记录（`Enrollment`，保证学生+课程唯一）。选课、课程、专业之间通过外键关联，删除时会进行业务校验防止破坏数据一致性。

系统启动时会自动写入示例专业、课程以及管理员账号，便于直接演示。

# 代码文件说明

下表按照包结构整理了核心类的作用，帮助快速熟悉代码：

| 所在包 | 主要文件 | 说明 |
| ------ | -------- | ---- |
| `annotation` | `Column.java` | 自定义注解，标记实体字段对应的数据库列名，便于 `ResultSetMapper` 通过反射填充属性。 |
| `common` | `PageResult.java` | 封装分页结果，包含记录列表、总数以及页码信息。 |
| `controller` | `LoginServlet.java`、`LogoutServlet.java`、`EmployeeServlet.java`、`OptionItemServlet.java` | Web 层控制器，分别负责登录、退出、员工管理、选项管理等请求的接收与跳转。 |
| `dao` | `BaseDao.java`、`AccountDao.java`、`EmployeeDao.java`、`OptionItemDao.java` | 数据访问层，封装 JDBC 操作；`BaseDao` 提供通用的增删改查模板，其余类针对各自表编写 SQL。 |
| `filter` | `AuthFilter.java` | 登录校验过滤器，拦截未登录用户访问业务页面。 |
| `model` | `Account.java`、`Employee.java`、`OptionItem.java` | 实体类，结合 `@Column` 注解描述数据库字段与 Java 属性。 |
| `service` | `AuthService.java`、`EmployeeService.java`、`OptionItemService.java` | 业务层，负责组合 DAO 操作、生成主键/编号、更新登录时间等逻辑。 |
| `util` | `ConnectionManager.java`、`IdGenerator.java`、`ResultSetMapper.java` | 工具类，包含 JDBC 连接管理、编号生成、`ResultSet` 到实体的映射。 |

> 想要了解数据库结构，可以参考 `src/main/resources/schema.sql`；静态资源与 JSP 页面位于 `src/main/webapp` 目录下。

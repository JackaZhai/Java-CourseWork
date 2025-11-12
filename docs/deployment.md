# 部署与演示指南

1. 安装 Java 17 运行环境与 Maven 3.9+。
2. 在项目根目录执行 `mvn clean package`，生成可执行的 `target/coursework-0.0.1-SNAPSHOT.jar`。
3. 在笔记本电脑上运行 `java -jar target/coursework-0.0.1-SNAPSHOT.jar` 启动系统，默认端口为 8080。
4. 浏览器访问 `http://localhost:8080`，使用系统提供的功能进行演示。
5. 管理员初始账号为 `admin`，密码为 `Admin@123`。
6. H2 内存数据库会在启动时自动初始化示例数据，无需额外配置，可通过 `http://localhost:8080/h2-console` 查看。

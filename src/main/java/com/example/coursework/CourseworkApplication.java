package com.example.coursework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 整个应用程序的启动入口。
 * 使用 {@link SpringBootApplication} 注解后，Spring Boot 会自动完成组件扫描、自动配置等工作。
 */
@SpringBootApplication
public class CourseworkApplication {

    /**
     * 通过 SpringApplication 启动内嵌容器，运行学生选课管理系统。
     *
     * @param args 启动参数，可用于调整 Spring Boot 行为
     */
    public static void main(String[] args) {
        SpringApplication.run(CourseworkApplication.class, args);
    }
}

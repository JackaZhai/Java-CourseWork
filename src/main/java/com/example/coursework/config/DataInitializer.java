package com.example.coursework.config;

import com.example.coursework.model.Course;
import com.example.coursework.model.Program;
import com.example.coursework.model.Role;
import com.example.coursework.repository.CourseRepository;
import com.example.coursework.repository.ProgramRepository;
import com.example.coursework.service.AccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 系统初始化配置类，用于在应用启动时批量导入基础数据。
 */
@Configuration
public class DataInitializer {

    /**
     * CommandLineRunner 在 Spring Boot 启动完成后执行，用于初始化基础数据。
     *
     * @param programRepository 专业信息仓储
     * @param courseRepository  课程信息仓储
     * @param accountService    账号服务，用于创建管理员账号
     * @return 包含初始化逻辑的 Lambda 表达式
     */
    @Bean
    public CommandLineRunner initData(ProgramRepository programRepository,
                                      CourseRepository courseRepository,
                                      AccountService accountService) {
        return args -> {
            // 若库中没有任何专业，则插入两条示例数据，便于快速体验系统
            if (programRepository.count() == 0) {
                Program cs = new Program();
                cs.setCode("CS");
                cs.setName("计算机科学与技术");
                cs.setDescription("培养具有软件开发与系统设计能力的复合型人才。");
                programRepository.save(cs);

                Program fin = new Program();
                fin.setCode("FIN");
                fin.setName("金融学");
                fin.setDescription("专注金融市场分析与投资管理。");
                programRepository.save(fin);
            }
            // 若没有课程数据，同样生成示例课程
            if (courseRepository.count() == 0) {
                Course java = new Course();
                java.setCode("CS101");
                java.setName("Java 程序设计");
                java.setCredits(4);
                java.setDescription("掌握 Java 语言基础与面向对象编程思想。");
                courseRepository.save(java);

                Course db = new Course();
                db.setCode("CS201");
                db.setName("数据库系统");
                db.setCredits(3);
                db.setDescription("学习关系数据库设计与 SQL 编程。");
                courseRepository.save(db);

                Course econ = new Course();
                econ.setCode("FIN110");
                econ.setName("宏观经济学");
                econ.setCredits(3);
                econ.setDescription("理解宏观经济运行机制与政策调控。");
                courseRepository.save(econ);
            }
            // 确保存在一个管理员账号，便于后续管理操作
            if (!accountService.exists("admin")) {
                accountService.createAccount("admin", "Admin@123", Role.ADMIN, true);
            }
        };
    }
}

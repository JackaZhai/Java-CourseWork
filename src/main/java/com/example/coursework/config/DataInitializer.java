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

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(ProgramRepository programRepository,
                                      CourseRepository courseRepository,
                                      AccountService accountService) {
        return args -> {
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
            if (!accountService.exists("admin")) {
                accountService.createAccount("admin", "Admin@123", Role.ADMIN, true);
            }
        };
    }
}

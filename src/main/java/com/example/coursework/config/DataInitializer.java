package com.example.coursework.config;

import com.example.coursework.model.Account;
import com.example.coursework.model.Course;
import com.example.coursework.model.Major;
import com.example.coursework.model.Role;
import com.example.coursework.repository.AccountRepository;
import com.example.coursework.repository.CourseRepository;
import com.example.coursework.repository.MajorRepository;
import com.example.coursework.util.PasswordUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seed(MajorRepository majorRepository, CourseRepository courseRepository, AccountRepository accountRepository) {
        return args -> {
            if (majorRepository.count() == 0) {
                Major cs = new Major();
                cs.setCode("CS");
                cs.setName("计算机科学");
                cs.setDescription("计算机科学与技术专业");
                majorRepository.save(cs);

                Major se = new Major();
                se.setCode("SE");
                se.setName("软件工程");
                se.setDescription("软件工程专业");
                majorRepository.save(se);
            }

            if (courseRepository.count() == 0) {
                Course java = new Course();
                java.setCode("JAVA101");
                java.setName("Java 程序设计");
                java.setDescription("面向对象与基础");
                java.setCredits(3);
                courseRepository.save(java);

                Course db = new Course();
                db.setCode("DB201");
                db.setName("数据库系统");
                db.setDescription("关系数据库理论");
                db.setCredits(4);
                courseRepository.save(db);
            }

            if (accountRepository.findByUsername("admin").isEmpty()) {
                Account admin = new Account();
                admin.setUsername("admin");
                admin.setPassword(PasswordUtil.md5("admin123!"));
                admin.setRole(Role.ADMIN);
                admin.setMustChangePassword(false);
                accountRepository.save(admin);
            }
        };
    }
}

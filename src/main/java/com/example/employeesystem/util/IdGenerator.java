package com.example.employeesystem.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 应用内部使用的编号/主键生成工具。
 */
public final class IdGenerator {
    private static final DateTimeFormatter YEAR_FORMAT = DateTimeFormatter.ofPattern("yyyy");

    private IdGenerator() {
    }

    public static String uuid() {
        // 生成去除连字符的 32 位 UUID，便于作为主键使用
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String employeeCode(String jobCode, LocalDate hireDate, String lastCode) {
        // 员工编号由“岗位编号 + 入职年份 + 四位序号”组成
        String year = hireDate.format(YEAR_FORMAT);
        String prefix = jobCode + year;
        int nextSequence = 1;
        if (lastCode != null && lastCode.startsWith(prefix)) {
            // 如果已经存在相同前缀的编号，则在其基础上自增
            String sequencePart = lastCode.substring(prefix.length());
            nextSequence = Integer.parseInt(sequencePart) + 1;
        }
        return prefix + String.format("%04d", nextSequence);
    }
}

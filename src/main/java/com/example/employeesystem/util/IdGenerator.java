package com.example.employeesystem.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public final class IdGenerator {
    private static final DateTimeFormatter YEAR_FORMAT = DateTimeFormatter.ofPattern("yyyy");

    private IdGenerator() {
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String employeeCode(String jobCode, LocalDate hireDate, String lastCode) {
        String year = hireDate.format(YEAR_FORMAT);
        String prefix = jobCode + year;
        int nextSequence = 1;
        if (lastCode != null && lastCode.startsWith(prefix)) {
            String sequencePart = lastCode.substring(prefix.length());
            nextSequence = Integer.parseInt(sequencePart) + 1;
        }
        return prefix + String.format("%04d", nextSequence);
    }
}

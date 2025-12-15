package com.example.coursework.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

public final class PasswordUtil {
    private static final Pattern COMPLEXITY = Pattern.compile("^(?=.*[0-9])(?=.*[A-Za-z])(?=.*[^A-Za-z0-9]).{8,}$");

    private PasswordUtil() {
    }

    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm missing", e);
        }
    }

    public static boolean isComplex(String password) {
        return COMPLEXITY.matcher(password).matches();
    }
}

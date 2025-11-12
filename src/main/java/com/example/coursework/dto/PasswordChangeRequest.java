package com.example.coursework.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 密码修改表单对象，配合 Spring Validation 完成字段校验。
 */
public class PasswordChangeRequest {

    /**
     * 旧密码，不能为空。
     */
    @NotBlank
    private String oldPassword;

    /**
     * 新密码，必须包含字母、数字和特殊字符，且长度不少于 8 位。
     */
    @NotBlank
    @Size(min = 8)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
            message = "Password must include letters, numbers, and special characters")
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}

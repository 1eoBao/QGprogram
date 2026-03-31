// RegisterDTO.java
package com.dormitory.dto;

import lombok.Data;
import javax.validation.constraints.Pattern;

@Data
public class RegisterDTO {
    @Pattern(regexp = "^(3125|3225)\\d{6}$", message = "学号格式错误")
    private String account;

    private String password;
    private String confirmPassword;
    private String role;
    private String name;
    private String phone;
}
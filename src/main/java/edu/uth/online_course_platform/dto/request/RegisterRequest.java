package edu.uth.online_course_platform.dto.request;

import edu.uth.online_course_platform.models.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    private String phoneNumber;

    // Trường này cho phép client chỉ định vai trò khi đăng ký (STUDENT hoặc INSTRUCTOR)
    private User.UserRole role;
}
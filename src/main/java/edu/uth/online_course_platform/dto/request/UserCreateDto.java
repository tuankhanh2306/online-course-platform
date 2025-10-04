package edu.uth.online_course_platform.dto.request;

import edu.uth.online_course_platform.models.User;
import lombok.Data;

@Data
public class UserCreateDto {
    private String fullName;
    private String phoneNumber;
    private String email;
    private String password;
    private User.UserRole role;
}


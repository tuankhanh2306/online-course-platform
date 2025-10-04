package edu.uth.online_course_platform.dto.response;

import edu.uth.online_course_platform.models.User;
import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class UserResponseDto {
    private long userId;
    private String fullName;
    private String phoneNumber;
    private String email;
    private User.UserRole role;

    public static UserResponseDto fromEntity(User user) {
        return UserResponseDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .build();
    }
}

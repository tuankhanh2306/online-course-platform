package edu.uth.online_course_platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentEnrollmentResponse {
    private Long studentId;
    private String fullName;
    private String email;
    private LocalDateTime enrolledAt; // Thời gian đăng ký (tùy chọn, nhưng hữu ích)
}
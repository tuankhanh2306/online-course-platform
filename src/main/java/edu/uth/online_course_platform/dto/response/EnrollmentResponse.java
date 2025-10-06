package edu.uth.online_course_platform.dto.response;

import edu.uth.online_course_platform.models.Enrollment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter @Setter @AllArgsConstructor
public class EnrollmentResponse {
    private Long enrollmentId;
    private String studentName;
    private Long courseId;
    private String courseName;
    private LocalDateTime enrolledAt;
    private Enrollment.EnrollmentStatus enrollmentStatus;
    private Double progress;
}

package edu.uth.online_course_platform.dto.response;

import edu.uth.online_course_platform.models.Enrollment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter @Setter @AllArgsConstructor @Builder
public class EnrollmentResponse {
    private Long enrollmentId;
    private String studentName;
    private String studentId;
    private Long courseId;
    private String courseName;
    private String courseTitle;
    private LocalDateTime enrolledAt;
    private Enrollment.EnrollmentStatus enrollmentStatus; // Trạng thái học tập
    private Double progress;

    // *** THÊM TRƯỜNG NÀY ***
    private String paymentStatus; // Trạng thái thanh toán (PENDING, SUCCESS, FAILED)

    // *** (Tùy chọn) Bổ sung thêm các trường nếu cần cho my_courses.html ***
    private String instructorName;
    private String imageUrl;
}

package edu.uth.online_course_platform.controllers;

import edu.uth.online_course_platform.dto.response.ApiResponse;
import edu.uth.online_course_platform.dto.response.EnrollmentResponse;
import edu.uth.online_course_platform.dto.response.LessonResponse;
import edu.uth.online_course_platform.services.EnrollmentService;
import edu.uth.online_course_platform.services.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final LessonService lessonService; // Inject LessonService

    @PostMapping("/courses/{courseId}/enroll")
    @PreAuthorize("hasRole('STUDENT')") // Chỉ user có role STUDENT mới được gọi API này
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enrollInCourse(@PathVariable Long courseId) {
        EnrollmentResponse enrollmentResponse = enrollmentService.createEnrollment(courseId);

        ApiResponse<EnrollmentResponse> apiResponse = ApiResponse.<EnrollmentResponse>builder()
                .code(200)
                .message("Enrolled in the course successfully. Please proceed to payment.")
                .result(enrollmentResponse)
                .build();
        return ResponseEntity.ok(apiResponse);
    }


    // API lấy bài học cho khóa đã đăng ký
    @GetMapping("/courses/{courseId}/lessons")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<LessonResponse>>> getLessonsForEnrolledCourse(@PathVariable Long courseId) {
        // ... (code giữ nguyên)
        List<LessonResponse> lessons = lessonService.getLessonsForEnrolledStudent(courseId);
        ApiResponse<List<LessonResponse>> apiResponse = ApiResponse.<List<LessonResponse>>builder()
                .code(200)
                .message("Lessons fetched successfully for enrolled student.")
                .result(lessons)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * THÊM ENDPOINT NÀY
     * API cho phép Học viên (hoặc Admin) hủy một lượt đăng ký.
     */
    @DeleteMapping("/enrollments/{enrollmentId}")
    // Cho phép STUDENT hoặc ADMIN gọi
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> cancelEnrollment(@PathVariable Long enrollmentId) {
        enrollmentService.deleteEnrollment(enrollmentId);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(200) // Hoặc 204 No Content nếu không có message
                .message("Hủy đăng ký thành công.")
                .build();
        return ResponseEntity.ok(apiResponse);
        // Hoặc trả về noContent() nếu bạn không cần message:
        // return ResponseEntity.noContent().build();
    }
}
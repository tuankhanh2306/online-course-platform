package edu.uth.online_course_platform.controllers;

import edu.uth.online_course_platform.dto.response.ApiResponse;
import edu.uth.online_course_platform.dto.response.CourseResponse;
import edu.uth.online_course_platform.services.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')") // Chỉ Admin mới có quyền truy cập controller này
@RequiredArgsConstructor
public class AdminController {

    private final CourseService courseService;

    // API để Admin xem tất cả các khóa học đang chờ duyệt
    @GetMapping("/courses/pending")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getPendingCourses() {
        List<CourseResponse> courses = courseService.getPendingApprovalCourses();
        return ResponseEntity.ok(new ApiResponse<>(200, "Pending courses fetched successfully", courses));
    }

    // API để Admin phê duyệt một khóa học
    @PostMapping("/courses/{courseId}/approve")
    public ResponseEntity<ApiResponse<String>> approveCourse(@PathVariable Long courseId) {
        courseService.approveCourse(courseId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Course approved and published.", null));
    }

    // API để Admin từ chối một khóa học
    @PostMapping("/courses/{courseId}/reject")
    public ResponseEntity<ApiResponse<String>> rejectCourse(@PathVariable Long courseId) {
        courseService.rejectCourse(courseId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Course rejected.", null));
    }
}
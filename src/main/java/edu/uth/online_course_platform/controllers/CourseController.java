package edu.uth.online_course_platform.controllers;

import edu.uth.online_course_platform.dto.request.CreateCourseRequest;
import edu.uth.online_course_platform.dto.request.CreateLessonRequest;
import edu.uth.online_course_platform.dto.request.UpdateCourseRequest;
import edu.uth.online_course_platform.dto.response.ApiResponse;
import edu.uth.online_course_platform.dto.response.CourseResponse;
import edu.uth.online_course_platform.dto.response.InstructorRevenueResponse;
import edu.uth.online_course_platform.dto.response.LessonResponse;
import edu.uth.online_course_platform.models.Course;
import edu.uth.online_course_platform.models.Lesson;
import edu.uth.online_course_platform.services.CourseService;
import edu.uth.online_course_platform.services.LessonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
@RequestMapping("/api/instructor/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private LessonService lessonService;

    // Create new Course:
    @PostMapping("/")
    public ResponseEntity<ApiResponse> createNewCourse(@Valid @RequestBody CreateCourseRequest createCourseRequest) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Khóa học được tạo thành công");
        apiResponse.setCode(200);
        apiResponse.setResult(courseService.createNewCourse(createCourseRequest));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    // Get all courses of current instructor:
    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<Course>>> getListCoursesOfInstructor() {
        ApiResponse<List<Course>> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Lấy Danh sách khóa học thành công");
        apiResponse.setCode(200);
        apiResponse.setResult(courseService.getAllCourseOfInstructor());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    // Get List of lessons of a instructor's course:
    @GetMapping("/{courseId}/lessons")
    public ResponseEntity<ApiResponse<List<Lesson>>> getListLessonsOfCourse(@PathVariable Long courseId) {
        ApiResponse<List<Lesson>> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Lấy Danh sách bài học thành công");
        apiResponse.setCode(200);
        apiResponse.setResult(courseService.getListLessonsByCourse(courseId));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    // Create a new lesson in a course:
    @PostMapping("/{courseId}/lessons")
    public ResponseEntity<ApiResponse<LessonResponse>> createNewLesson(@PathVariable Long courseId, @RequestBody CreateLessonRequest createLessonRequest) {
        ApiResponse<LessonResponse> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Bài học được tạo thành công");
        apiResponse.setCode(200);
        try {
            apiResponse.setResult(courseService.createNewLesson(courseId, createLessonRequest));
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(403, "Bạn không được phép sửa đổi khóa học này", null));
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    // giảng viên gửi khóa học đi duyệt
    @PostMapping("/{courseId}/submit")
    public ResponseEntity<ApiResponse<String>> submitCourseForApproval(@PathVariable Long courseId) {
        courseService.submitCourse(courseId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Khóa học đã được gửi để phê duyệt thành công.", null));
    }

    /**
     * API cho Giảng viên xem tổng quan doanh thu.
     */
    @GetMapping("/revenue")
    @PreAuthorize("hasRole('INSTRUCTOR')") // Chỉ Giảng viên mới xem được
    public ResponseEntity<ApiResponse<InstructorRevenueResponse>> getMyRevenue() {
        InstructorRevenueResponse revenueData = courseService.getInstructorRevenueDashboard();

        return ResponseEntity.ok(
                new ApiResponse<>(200, "Lấy dữ liệu doanh thu thành công", revenueData)
        );
    }

    /**
     * API cho phép Giảng viên cập nhật khóa học.
     */
    @PutMapping("/{courseId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody UpdateCourseRequest request) {

        CourseResponse updatedCourse = courseService.updateCourse(courseId, request);

        return ResponseEntity.ok(
                new ApiResponse<>(200, "Cập nhật khóa học thành công, đã chuyển về trạng thái Draft.", updatedCourse)
        );
    }
}

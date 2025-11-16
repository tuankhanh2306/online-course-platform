package edu.uth.online_course_platform.controllers;

import edu.uth.online_course_platform.dto.request.CreateCourseRequest;
import edu.uth.online_course_platform.dto.request.CreateLessonRequest;
import edu.uth.online_course_platform.dto.request.UpdateCourseRequest;
import edu.uth.online_course_platform.dto.request.UpdateLessonRequest; // Thêm import này
import edu.uth.online_course_platform.dto.response.ApiResponse;
import edu.uth.online_course_platform.dto.response.CourseResponse;
import edu.uth.online_course_platform.dto.response.InstructorRevenueResponse;
import edu.uth.online_course_platform.dto.response.LessonResponse;
import edu.uth.online_course_platform.services.CourseService;
import edu.uth.online_course_platform.services.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor; // Thêm import này nếu chưa có
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructor/courses") // Base path cho tất cả API của instructor về khóa học
@PreAuthorize("hasAnyRole('INSTRUCTOR')") // Chỉ giảng viên mới được truy cập các API này
@RequiredArgsConstructor // Sử dụng Lombok để tự động tạo constructor
public class CourseController {

    private final CourseService courseService;
    private final LessonService lessonService; // Inject LessonService

    // Create new Course:
    @PostMapping("/")
    public ResponseEntity<ApiResponse<CourseResponse>> createNewCourse(@Valid @RequestBody CreateCourseRequest createCourseRequest) {
        CourseResponse newCourse = courseService.createNewCourse(createCourseRequest);
        return new ResponseEntity<>(new ApiResponse<>(201, "Khóa học được tạo thành công", newCourse), HttpStatus.CREATED); // HTTP 201 Created
    }

    // Get all courses of current instructor:
    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getListCoursesOfInstructor() {
        List<CourseResponse> courses = courseService.getAllCourseOfInstructor();
        return new ResponseEntity<>(new ApiResponse<>(200, "Lấy Danh sách khóa học thành công", courses), HttpStatus.OK);
    }

    // Get instructor's course details by ID (for edit form) - Bổ sung nghiệp vụ
    @GetMapping("/{courseId}")
    public ResponseEntity<ApiResponse<CourseResponse>> getInstructorCourseDetails(@PathVariable Long courseId) {
        CourseResponse courseDetails = courseService.getInstructorCourseDetails(courseId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy chi tiết khóa học thành công", courseDetails));
    }

    // Update instructor's course by ID - Nghiệp vụ đã có
    @PutMapping("/{courseId}")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody UpdateCourseRequest request) {
        CourseResponse updatedCourse = courseService.updateCourse(courseId, request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật khóa học thành công, đã chuyển về trạng thái Draft.", updatedCourse));
    }

    // Submit course for approval - Nghiệp vụ đã có
    @PostMapping("/{courseId}/submit")
    public ResponseEntity<ApiResponse<String>> submitCourseForApproval(@PathVariable Long courseId) {
        courseService.submitCourse(courseId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Khóa học đã được gửi để phê duyệt thành công.", null));
    }

    // Get instructor's revenue dashboard - Nghiệp vụ đã có
    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<InstructorRevenueResponse>> getMyRevenue() {
        InstructorRevenueResponse revenueData = courseService.getInstructorRevenueDashboard();
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy dữ liệu doanh thu thành công", revenueData));
    }

    // ===== LESSON MANAGEMENT API (TRONG CÙNG CONTROLLER CỦA INSTRUCTOR) =====

    // Get List of lessons of a instructor's course: - Nghiệp vụ đã có
    @GetMapping("/{courseId}/lessons")
    public ResponseEntity<ApiResponse<List<LessonResponse>>> getListLessonsOfCourse(@PathVariable Long courseId) {
        List<LessonResponse> lessons = courseService.getListLessonsByCourse(courseId); // Gọi CourseService để lấy list lessons của instructor
        return new ResponseEntity<>(new ApiResponse<>(200, "Lấy Danh sách bài học thành công", lessons), HttpStatus.OK);
    }

    // Create a new lesson in a course: - Nghiệp vụ đã có
    @PostMapping("/{courseId}/lessons")
    public ResponseEntity<ApiResponse<LessonResponse>> createNewLesson(
            @PathVariable Long courseId,
            @Valid @RequestBody CreateLessonRequest createLessonRequest) throws IllegalAccessException { // Thêm throws IllegalAccessException
        LessonResponse newLesson = courseService.createNewLesson(courseId, createLessonRequest); // Gọi CourseService
        return new ResponseEntity<>(new ApiResponse<>(201, "Bài học được tạo thành công", newLesson), HttpStatus.CREATED);
    }

    // Get lesson details by ID (for edit form) - Bổ sung nghiệp vụ
    @GetMapping("/lessons/{lessonId}") // Lưu ý URL path
    public ResponseEntity<ApiResponse<LessonResponse>> getLessonDetails(@PathVariable Long lessonId) {
        LessonResponse lessonDetails = lessonService.getInstructorLessonDetails(lessonId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy chi tiết bài học thành công", lessonDetails));
    }

    // Update a lesson in a course: - Bổ sung nghiệp vụ
    @PutMapping("/lessons/{lessonId}") // Lưu ý URL path
    public ResponseEntity<ApiResponse<LessonResponse>> updateLesson(
            @PathVariable Long lessonId,
            @Valid @RequestBody UpdateLessonRequest request) throws IllegalAccessException {
        LessonResponse updatedLesson = lessonService.updateLesson(lessonId, request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Bài học được cập nhật thành công.", updatedLesson));
    }

    // Delete a lesson in a course: - Bổ sung nghiệp vụ
    @DeleteMapping("/lessons/{lessonId}") // Lưu ý URL path
    public ResponseEntity<ApiResponse<String>> deleteLesson(@PathVariable Long lessonId) throws IllegalAccessException {
        lessonService.deleteLesson(lessonId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Bài học đã được xóa thành công.", null));
    }
}
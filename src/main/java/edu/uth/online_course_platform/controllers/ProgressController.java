package edu.uth.online_course_platform.controllers;

import edu.uth.online_course_platform.dto.response.ApiResponse;
import edu.uth.online_course_platform.services.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/progress")
@PreAuthorize("hasRole('STUDENT')") // Chỉ học viên mới có thể quản lý tiến độ của mình
public class ProgressController {

    private final ProgressService progressService;

    /**
     * API để học viên đánh dấu một bài học là đã hoàn thành.
     */
    @PostMapping("/lessons/{lessonId}/complete")
    public ResponseEntity<ApiResponse<String>> completeLesson(@PathVariable Long lessonId) {
        progressService.completeLesson(lessonId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Bài học đã được đánh dấu là hoàn thành.", null));
    }

    /**
     * API để lấy danh sách các Lesson ID đã hoàn thành của một học viên cho một khóa học.
     * Sử dụng để tải trạng thái ban đầu của các bài học trên giao diện.
     */
    @GetMapping("/courses/{courseId}/completed-lessons")
    public ResponseEntity<ApiResponse<List<Long>>> getCompletedLessonsForCourse(@PathVariable Long courseId) {
        List<Long> completedLessonIds = progressService.getCompletedLessonIdsForCourse(courseId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách bài học đã hoàn thành thành công.", completedLessonIds));
    }
}
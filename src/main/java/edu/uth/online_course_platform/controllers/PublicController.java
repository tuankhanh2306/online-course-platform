// Tạo file controllers/PublicController.java
package edu.uth.online_course_platform.controllers;

import edu.uth.online_course_platform.dto.response.ApiResponse;
import edu.uth.online_course_platform.dto.response.CourseResponse;
import edu.uth.online_course_platform.services.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final CourseService courseService;

    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getAllPublishedCourses() {
        List<CourseResponse> courses = courseService.getPublishedCourses();
        return ResponseEntity.ok(new ApiResponse<>(200, "Published courses fetched successfully", courses));
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<ApiResponse<CourseResponse>> getPublishedCourseDetails(@PathVariable Long courseId) {
        CourseResponse course = courseService.getPublishedCourseById(courseId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Course details fetched successfully", course));
    }
}
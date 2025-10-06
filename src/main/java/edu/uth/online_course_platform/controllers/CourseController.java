package edu.uth.online_course_platform.controllers;

import edu.uth.online_course_platform.dto.request.CreateCourseRequest;
import edu.uth.online_course_platform.dto.request.CreateLessonRequest;
import edu.uth.online_course_platform.dto.response.ApiResponse;
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
        apiResponse.setMessage("Course created successfully");
        apiResponse.setCode(200);
        apiResponse.setResult(courseService.createNewCourse(createCourseRequest));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    // Get all courses of current instructor:
    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<Course>>> getListCoursesOfInstructor() {
        ApiResponse<List<Course>> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Course list successfully");
        apiResponse.setCode(200);
        apiResponse.setResult(courseService.getAllCourseOfInstructor());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    // Get List of lessons of a instructor's course:
    @GetMapping("/{courseId}/lessons")
    public ResponseEntity<ApiResponse<List<Lesson>>> getListLessonsOfCourse(@PathVariable Long courseId) {
        ApiResponse<List<Lesson>> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Lesson list successfully");
        apiResponse.setCode(200);
        apiResponse.setResult(courseService.getListLessonsByCourse(courseId));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    // Create a new lesson in a course:
    @PostMapping("/{courseId}/lessons")
    public ResponseEntity<ApiResponse<LessonResponse>> createNewLesson(@PathVariable Long courseId, @RequestBody CreateLessonRequest createLessonRequest) {
        ApiResponse<LessonResponse> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Lesson created successfully");
        apiResponse.setCode(200);
        try {
            apiResponse.setResult(courseService.createNewLesson(courseId, createLessonRequest));
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(403, "You are not allowed to modify this course", null));
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}

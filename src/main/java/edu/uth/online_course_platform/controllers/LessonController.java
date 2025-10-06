package edu.uth.online_course_platform.controllers;

import edu.uth.online_course_platform.dto.request.UpdateLessonRequest;
import edu.uth.online_course_platform.dto.response.ApiResponse;
import edu.uth.online_course_platform.dto.response.LessonResponse;
import edu.uth.online_course_platform.models.Lesson;
import edu.uth.online_course_platform.services.LessonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
@RequestMapping("/api/instructor/lessons")
public class LessonController {
    @Autowired
    LessonService lessonService;

    @PutMapping("/{lessonId}")
    public ResponseEntity<ApiResponse<LessonResponse>> updateLesson(@PathVariable("lessonId") Long lessonId,@Valid @RequestBody UpdateLessonRequest updateRequest) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setCode(200);
        apiResponse.setMessage("success");
        try {
            apiResponse.setResult(lessonService.updateLesson(lessonId, updateRequest));
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(403, "You are not allowed to modify this lesson", null));
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{lessonId}")
    public ResponseEntity<ApiResponse> deleteLesson(@PathVariable("lessonId") Long lessonId) {
        ApiResponse<Lesson> apiResponse = new ApiResponse<>();
        apiResponse.setCode(200);
        apiResponse.setMessage("success");
        try {
            lessonService.deleteLesson(lessonId);
        } catch(IllegalAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(403, "You are not allowed to modify this lesson", null));
        };
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}

package edu.uth.online_course_platform.controllers;

import edu.uth.online_course_platform.dto.request.CreateFeedbackRequest;
import edu.uth.online_course_platform.dto.response.ApiResponse;
import edu.uth.online_course_platform.dto.response.FeedbackResponse;
import edu.uth.online_course_platform.services.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    // API cho phép HỌC VIÊN tạo một đánh giá mới cho khóa học
    @PostMapping("/courses/{courseId}/feedbacks")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<FeedbackResponse>> createFeedback(
            @PathVariable Long courseId,
            @Valid @RequestBody CreateFeedbackRequest request) {

        FeedbackResponse feedbackResponse = feedbackService.createFeedback(courseId, request);
        ApiResponse<FeedbackResponse> apiResponse = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "Feedback submitted successfully.",
                feedbackResponse
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    // API CÔNG KHAI cho phép BẤT KỲ AI xem tất cả đánh giá của một khóa học
    @GetMapping("/public/courses/{courseId}/feedbacks")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getFeedbacksByCourse(@PathVariable Long courseId) {
        List<FeedbackResponse> feedbacks = feedbackService.getFeedbacksForCourse(courseId);
        ApiResponse<List<FeedbackResponse>> apiResponse = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Feedbacks fetched successfully.",
                feedbacks
        );
        return ResponseEntity.ok(apiResponse);
    }
}
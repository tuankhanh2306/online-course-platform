package edu.uth.online_course_platform.services;

import edu.uth.online_course_platform.dto.request.CreateFeedbackRequest;
import edu.uth.online_course_platform.dto.response.FeedbackResponse;
import edu.uth.online_course_platform.exceptions.AppException;
import edu.uth.online_course_platform.exceptions.ErrorCode;
import edu.uth.online_course_platform.exceptions.ResourceNotFoundException;
import edu.uth.online_course_platform.models.Course;
import edu.uth.online_course_platform.models.Feedback;
import edu.uth.online_course_platform.models.User;
import edu.uth.online_course_platform.repositories.CourseRepository;
import edu.uth.online_course_platform.repositories.EnrollmentRepository;
import edu.uth.online_course_platform.repositories.FeedbackRepository;
import edu.uth.online_course_platform.until.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AuthorizationService authorizationService;

    @Transactional
    public FeedbackResponse createFeedback(Long courseId, CreateFeedbackRequest request) {
        // 1. Lấy thông tin học viên hiện tại
        User student = authorizationService.getCurrentUser();

        // 2. Tìm khóa học
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        // 3. Kiểm tra xem học viên có thực sự đã đăng ký khóa học này không
        boolean isEnrolled = enrollmentRepository.existsByStudentAndCourse(student, course);
        if (!isEnrolled) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // 4. Tạo và lưu feedback mới
        Feedback feedback = Feedback.builder()
                .course(course)
                .student(student)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Feedback savedFeedback = feedbackRepository.save(feedback);

        // 5. Trả về response DTO
        return FeedbackResponse.builder()
                .feedbackId(savedFeedback.getFeedbackId())
                .rating(savedFeedback.getRating())
                .comment(savedFeedback.getComment())
                .studentName(student.getFullName())
                .createdAt(savedFeedback.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> getFeedbacksForCourse(Long courseId) {
        // Kiểm tra xem khóa học có tồn tại không
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found with id: " + courseId);
        }

        List<Feedback> feedbacks = feedbackRepository.findByCourse_CourseIdOrderByCreatedAtDesc(courseId);

        return feedbacks.stream()
                .map(feedback -> FeedbackResponse.builder()
                        .feedbackId(feedback.getFeedbackId())
                        .rating(feedback.getRating())
                        .comment(feedback.getComment())
                        .studentName(feedback.getStudent().getFullName())
                        .createdAt(feedback.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
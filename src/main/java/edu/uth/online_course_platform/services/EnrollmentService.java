package edu.uth.online_course_platform.services;

import edu.uth.online_course_platform.dto.response.EnrollmentResponse;
import edu.uth.online_course_platform.exceptions.ResourceNotFoundException;
import edu.uth.online_course_platform.models.Course;
import edu.uth.online_course_platform.models.Enrollment;
import edu.uth.online_course_platform.models.User;
import edu.uth.online_course_platform.repositories.CourseRepository;
import edu.uth.online_course_platform.repositories.EnrollmentRepository;
import edu.uth.online_course_platform.until.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final PaymentService paymentService;
    private final AuthorizationService authorizationService;
    private final UserService userService;

    @Transactional
    public EnrollmentResponse createEnrollment(Long courseId) throws IllegalAccessException, BadRequestException {
        User student = authorizationService.verifyCurrentStudentUser(); // ném lỗi nếu không phải student

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        // không cho phép enroll nếu đã có (unique constraint cũng bảo vệ, nhưng check sớm để trả message rõ)
        Optional<Enrollment> exist = enrollmentRepository.findByStudent_UserIdAndCourse_CourseId(student.getUserId(), courseId);
        if (exist.isPresent()) {
            throw new BadRequestException("Student already enrolled to this course");
        }

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .enrolledAt(LocalDateTime.now())
                .progress(0.0)
                .status(Enrollment.EnrollmentStatus.INACTIVE) // chưa active cho đến khi thanh toán
                .build();

        Enrollment saved = enrollmentRepository.save(enrollment);

        // Student add Current enroll and the course into his own List of enrollments and courses:
        student.getEnrollments().add(saved);
        student.getCourses().add(course);

        // tạo payment PENDING
        paymentService.generatePaymentForEnrollment(saved);

        return toEnrollmentResponse(saved);
    }

    public List<EnrollmentResponse> getEnrollmentsForCurrentUser() throws IllegalAccessException{
        User student = authorizationService.verifyCurrentStudentUser();
        List<Enrollment> enrollments = enrollmentRepository.findByStudent_UserId(student.getUserId());
        return enrollments.stream().map(this::toEnrollmentResponse).toList();
    }

    private EnrollmentResponse toEnrollmentResponse(Enrollment e) {
        return new EnrollmentResponse(
                e.getEnrollmentId(),
                e.getStudent().getFullName(),
                e.getCourse().getCourseId(),
                e.getCourse().getTitle(),
                e.getEnrolledAt(),
                e.getStatus(),
                e.getProgress()
        );
    }
}
package edu.uth.online_course_platform.services;

import edu.uth.online_course_platform.dto.response.EnrollmentResponse;
import edu.uth.online_course_platform.exceptions.AppException;
import edu.uth.online_course_platform.exceptions.ErrorCode;
import edu.uth.online_course_platform.exceptions.ResourceNotFoundException;
import edu.uth.online_course_platform.models.*;
import edu.uth.online_course_platform.repositories.CourseRepository;
import edu.uth.online_course_platform.repositories.EnrollmentRepository;
import edu.uth.online_course_platform.repositories.PaymentRepository;
import edu.uth.online_course_platform.until.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // Thêm import này nếu bạn đã xóa nó

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PaymentRepository paymentRepository;
    private final AuthorizationService authorizationService;

    @Transactional
    public EnrollmentResponse createEnrollment(Long courseId) {
        User student = authorizationService.getCurrentUser();
        if (student.getRole() != User.UserRole.STUDENT) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        enrollmentRepository.findByStudentAndCourse(student, course).ifPresent(enrollment -> {
            throw new AppException(ErrorCode.ENROLLMENT_EXISTED);
        });

        Enrollment newEnrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .enrolledAt(LocalDateTime.now())
                .status(Enrollment.EnrollmentStatus.ACTIVE)
                .progress(0.0)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(newEnrollment);

        Payment newPayment = Payment.builder()
                .enrollment(savedEnrollment)
                .amount(course.getPrice())
                .status(Payment.PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        paymentRepository.save(newPayment);

        // Đảm bảo DTO của bạn khớp với các trường này
        return EnrollmentResponse.builder()
                .enrollmentId(savedEnrollment.getEnrollmentId())
                .studentName(savedEnrollment.getStudent().getFullName())
                .studentId(String.valueOf(savedEnrollment.getStudent().getUserId()))
                .courseId(savedEnrollment.getCourse().getCourseId())
                .courseName(savedEnrollment.getCourse().getTitle())
                .courseTitle(savedEnrollment.getCourse().getTitle())
                .enrolledAt(savedEnrollment.getEnrolledAt())
                .enrollmentStatus(savedEnrollment.getStatus())
                .progress(savedEnrollment.getProgress())
                .build();
    }

    // ... (Giữ nguyên phương thức getEnrollmentsForCurrentUser) ...
    /**
     * *** CẬP NHẬT PHƯƠNG THỨC NÀY ***
     * Lấy danh sách các khóa học mà người dùng hiện tại đã đăng ký,
     * bao gồm cả trạng thái thanh toán mới nhất.
     */
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollmentsForCurrentUser() {
        User student = authorizationService.getCurrentUser();
        List<Enrollment> enrollments = enrollmentRepository.findByStudent(student);

        return enrollments.stream()
                .map(enrollment -> {
                    // Lấy trạng thái thanh toán mới nhất
                    Optional<Payment> latestPaymentOpt = paymentRepository.findTopByEnrollmentOrderByCreatedAtDesc(enrollment);
                    String paymentStatusString = latestPaymentOpt
                            .map(payment -> payment.getStatus().name()) // Lấy tên Enum (PENDING, SUCCESS, FAILED)
                            .orElse("UNKNOWN"); // Trạng thái mặc định nếu không có payment nào

                    // Lấy thông tin giảng viên và ảnh (nếu có)
                    String instructorName = (enrollment.getCourse() != null && enrollment.getCourse().getInstructor() != null)
                            ? enrollment.getCourse().getInstructor().getFullName() : "Không rõ";
                    String imageUrl = (enrollment.getCourse() != null) ? enrollment.getCourse().getImageUrl() : null;


                    // Xây dựng DTO với thông tin đầy đủ
                    return EnrollmentResponse.builder()
                            .enrollmentId(enrollment.getEnrollmentId())
                            .studentName(enrollment.getStudent().getFullName())
                            .studentId(String.valueOf(enrollment.getStudent().getUserId()))
                            .courseId(enrollment.getCourse().getCourseId())
                            .courseName(enrollment.getCourse().getTitle())
                            .courseTitle(enrollment.getCourse().getTitle())
                            .enrolledAt(enrollment.getEnrolledAt())
                            .enrollmentStatus(enrollment.getStatus())
                            .progress(enrollment.getProgress())
                            .paymentStatus(paymentStatusString) // *** THÊM TRẠNG THÁI THANH TOÁN ***
                            .instructorName(instructorName)     // *** THÊM TÊN GIẢNG VIÊN ***
                            .imageUrl(imageUrl)                 // *** THÊM URL ẢNH ***
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteEnrollment(Long enrollmentId) {
        // 1. Lấy thông tin người dùng hiện tại
        User currentUser = authorizationService.getCurrentUser();

        // 2. Tìm lượt đăng ký
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lượt đăng ký với ID: " + enrollmentId));

        // 3. Kiểm tra quyền hạn
        // Chỉ chủ sở hữu (học viên) HOẶC Admin mới được xóa
        if (!enrollment.getStudent().getUserId().equals(currentUser.getUserId()) &&
                currentUser.getRole() != User.UserRole.ADMIN) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // 4. (Tùy chọn) Có thể thêm logic chỉ cho phép hủy nếu trạng thái thanh toán là PENDING
        // Ví dụ:
        // Payment lastPayment = paymentRepository.findTopByEnrollmentOrderByPaymentDateDesc(enrollment)
        //       .orElse(null); // Tìm payment mới nhất của enrollment này
        // if (lastPayment != null && lastPayment.getStatus() != Payment.PaymentStatus.PENDING) {
        //      throw new AppException(ErrorCode.VALIDATION_ERROR, "Chỉ có thể hủy đăng ký khi đang chờ thanh toán.");
        // }


        // 5. Thực hiện xóa
        // Lưu ý: Do có CascadeType.ALL, việc xóa Enrollment sẽ tự động xóa các Payment liên quan.
        enrollmentRepository.delete(enrollment);
    }
}
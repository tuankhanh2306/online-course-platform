package edu.uth.online_course_platform.repositories;

import edu.uth.online_course_platform.models.Course;
import edu.uth.online_course_platform.models.Enrollment;
import edu.uth.online_course_platform.models.Payment;
import edu.uth.online_course_platform.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends CrudRepository<Enrollment, Long> {
    Optional<Enrollment> findByStudent_UserIdAndCourse_CourseId(Long studentId, Long courseId);
    List<Enrollment> findByStudent_UserId(Long studentId);

    Optional<Object> findByStudentAndCourse(User student, Course course);

    boolean existsByStudentAndCourse(User student, Course course);

    List<Enrollment> findByStudent(User student);

    // NEW: Kiểm tra xem một học viên đã đăng ký khóa học với trạng thái thanh toán thành công chưa
    // SỬA DÒNG NÀY
// SỬA DÒNG NÀY
    // SỬA DÒNG NÀY
    boolean existsByStudent_UserIdAndCourse_CourseIdAndPayments_Status(Long studentId, Long courseId, Payment.PaymentStatus status);
}

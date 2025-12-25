package edu.uth.online_course_platform.repositories;

import edu.uth.online_course_platform.dto.response.StudentEnrollmentResponse;
import edu.uth.online_course_platform.models.Course;
import edu.uth.online_course_platform.models.Enrollment;
import edu.uth.online_course_platform.models.Payment;
import edu.uth.online_course_platform.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends CrudRepository<Enrollment, Long> {
    Optional<Enrollment> findByStudent_UserIdAndCourse_CourseId(Long studentId, Long courseId);
    List<Enrollment> findByStudent_UserId(Long studentId);

    Optional<Object> findByStudentAndCourse(User student, Course course);

    boolean existsByStudentAndCourse(User student, Course course);

    List<Enrollment> findByStudent(User student);

    //  Kiểm tra xem một học viên đã đăng ký khóa học với trạng thái thanh toán thành công chưa
    boolean existsByStudent_UserIdAndCourse_CourseIdAndPayments_Status(Long studentId, Long courseId, Payment.PaymentStatus status);

    @Query("SELECT new edu.uth.online_course_platform.dto.response.StudentEnrollmentResponse(u.userId, u.fullName, u.email, e.enrolledAt) " +
            "FROM Enrollment e JOIN e.student u " +
            "WHERE e.course.courseId = :courseId")
    List<StudentEnrollmentResponse> findStudentsByCourseId(@Param("courseId") Long courseId);
}

package edu.uth.online_course_platform.repositories;

import edu.uth.online_course_platform.models.Enrollment;
import edu.uth.online_course_platform.models.Payment;
import edu.uth.online_course_platform.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findFirstByEnrollmentAndStatus(Enrollment enrollment, Payment.PaymentStatus status);
    List<Payment> findAllByStatus(Payment.PaymentStatus status);
    List<Payment> findByEnrollment(Enrollment enrollment);


    Optional<Payment> findTopByEnrollmentOrderByCreatedAtDesc(Enrollment enrollment);
    /**
     * Tính tổng doanh thu cho một Giảng viên, chỉ tính các thanh toán đã THÀNH CÔNG.
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
            "JOIN p.enrollment e " +
            "JOIN e.course c " +
            "WHERE c.instructor = :instructor AND p.status = 'SUCCESS'")
    BigDecimal findTotalRevenueByInstructor(@Param("instructor") User instructor);

    /**
     * THÊM PHƯƠNG THỨC NÀY
     * Đếm tổng số lượt bán thành công cho một Giảng viên.
     */
    @Query("SELECT COUNT(p) FROM Payment p " +
            "JOIN p.enrollment e " +
            "JOIN e.course c " +
            "WHERE c.instructor = :instructor AND p.status = 'SUCCESS'")
    long countSuccessfulEnrollmentsByInstructor(@Param("instructor") User instructor);


}

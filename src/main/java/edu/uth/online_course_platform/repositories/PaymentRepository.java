package edu.uth.online_course_platform.repositories;

import edu.uth.online_course_platform.models.Enrollment;
import edu.uth.online_course_platform.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findFirstByEnrollmentAndStatus(Enrollment enrollment, Payment.PaymentStatus status);
    List<Payment> findAllByStatus(Payment.PaymentStatus status);
    List<Payment> findByEnrollment(Enrollment enrollment);
}

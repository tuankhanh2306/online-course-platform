package edu.uth.online_course_platform.services;

import edu.uth.online_course_platform.dto.response.PaymentResponse;
import edu.uth.online_course_platform.exceptions.ResourceNotFoundException;
import edu.uth.online_course_platform.models.Course;
import edu.uth.online_course_platform.models.Enrollment;
import edu.uth.online_course_platform.models.Payment;
import edu.uth.online_course_platform.repositories.CourseRepository;
import edu.uth.online_course_platform.repositories.EnrollmentRepository;
import edu.uth.online_course_platform.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

    /*Mot enrollment co the co nhieu payment,
    Mot payment neu dang thanh toan thi o trang thai pending
    Neu thanh toan thanh cong ma tong so tien da thanh toan trong danh sach payment cua enrollment hien tai chua bang course's price thi dat status la PARTIAL
    Neu payment vua thanh toan thanh cong va cong voi so tong so tien da thanh toan thanh cong truoc do = voi price cua course thi payment do ve trang thai completed
    Neu payment nao thanh toan khong thanh cong thi dat status la FAILED, va hien nhien se khong duoc cong vao tong so tien da thanh toan
     */

    /*
    De thay doi trang thai cua mot payment Pending, can:
    1. Tim duoc payment can thay doi status -> Don gian, trong danh sach Payment chi co duy nhat mot payment hien tai la pending, neu sau mot khoang thoi gian ma van con pending thi chuyen thanh failed.
    2. Quyet dinh gan cho no  partial hay completed hay failed
     */

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    // Tạo payment initial PENDING (khi enroll)
    @Transactional
    public PaymentResponse generatePaymentForEnrollment(Enrollment enrollment) {
        Course course = enrollment.getCourse();

        Payment payment = Payment.builder()
                .enrollment(enrollment)
                .amount(course.getPrice())
                .createdAt(LocalDateTime.now())
                .status(Payment.PaymentStatus.PENDING)
                .build();

        Payment saved = paymentRepository.save(payment);
        // also add to enrollment.payments if you keep both sides in memory
        enrollment.getPayments().add(saved);
        return toPaymentResponse(saved);
    }

    // Giả lập thành công: tìm payment PENDING và đánh dấu COMPLETED,
    // sau đó cập nhật tổng thanh toán của enrollment => nếu >= price -> set enrollment ACTIVE
    @Transactional
    public PaymentResponse simulatePaymentSuccess(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));

        // tìm một payment đang PENDING (nếu chỉ 1 PENDING, lấy nó)
        Payment payment = paymentRepository.findFirstByEnrollmentAndStatus(enrollment, Payment.PaymentStatus.PENDING)
                .orElseThrow(() -> new ResourceNotFoundException("No pending payment for enrollment"));

        // mark completed
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        paymentRepository.save(payment);

        // tính tổng đã thanh toán for this enrollment
        BigDecimal totalPaid = paymentRepository.findByEnrollment(enrollment).stream()
                .filter(p -> p.getStatus() == Payment.PaymentStatus.COMPLETED || p.getStatus() == Payment.PaymentStatus.PARTIAL)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal coursePrice = enrollment.getCourse().getPrice();

        if (totalPaid.compareTo(coursePrice) >= 0) {
            enrollment.setStatus(Enrollment.EnrollmentStatus.ACTIVE);
            enrollment.setProgress(0.0); // bắt đầu học
            enrollmentRepository.save(enrollment);
        } else {
            // nếu chưa đủ (kịch bản partial payment), set enrollment status or keep INACTIVE
            enrollment.setStatus(Enrollment.EnrollmentStatus.INACTIVE);
            enrollmentRepository.save(enrollment);
        }

        return toPaymentResponse(payment);
    }

    // helper
    private PaymentResponse toPaymentResponse(Payment payment) {
        return new PaymentResponse(
                payment.getPaymentId(),
                payment.getEnrollment().getEnrollmentId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }

    // Optional: scheduled job để mark PENDING -> FAILED if too old
    @Scheduled(fixedRateString = "${payments.pending-timeout-ms:1800000}") // configurable
    @Transactional
    public void expirePendingPayments() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(30); // hoặc cấu hình
        List<Payment> pendings = paymentRepository.findAllByStatus(Payment.PaymentStatus.PENDING);
        for (Payment p : pendings) {
            if (p.getCreatedAt().isBefore(threshold)) {
                p.setStatus(Payment.PaymentStatus.FAILED);
                paymentRepository.save(p);
            }
        }
    }
}
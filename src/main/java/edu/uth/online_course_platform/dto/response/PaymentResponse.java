package edu.uth.online_course_platform.dto.response;

import edu.uth.online_course_platform.models.Payment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class PaymentResponse {
    private Long paymentId;
    private Long enrollmentId;
    private BigDecimal amount;
    private Payment.PaymentStatus status;
    private LocalDateTime createdAt;
   // private String transactionId; // optional
}


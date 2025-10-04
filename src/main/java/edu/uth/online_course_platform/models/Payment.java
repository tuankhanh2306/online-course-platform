package edu.uth.online_course_platform.models;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long paymentId;

    @ManyToOne
    @JoinColumn(name = "enrollment_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Enrollment enrollment;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status = PaymentStatus.PENDING;

    public enum PaymentStatus {
        PENDING,
        SUCCESS,
        FAILED
    }
}


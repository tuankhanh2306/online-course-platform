package edu.uth.online_course_platform.models;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "enrollments", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "course_id"})
},
        indexes = {
                @Index(columnList = "student_id"),
                @Index(columnList = "course_id")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long enrollmentId;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private User student;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Course course;

    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Payment> payments =  new ArrayList<>();

    @CreatedDate
    @Column(name="enrolledAt", nullable = false)
    private LocalDateTime enrolledAt;

    @Column(name="progress", nullable = false)
    private Double progress = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name="status",  nullable = false)
    private EnrollmentStatus status;

    public enum EnrollmentStatus {INACTIVE, ACTIVE, COMPLETED, CANCELED}
}

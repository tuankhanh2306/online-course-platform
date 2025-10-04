package edu.uth.online_course_platform.models;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "enrollments", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "course_id"})
})
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

    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments;
}

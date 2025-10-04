package edu.uth.online_course_platform.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "feedbacks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long feedbackId;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private User student;

    @Column(nullable = false)
    private int rating;

    @Column(columnDefinition = "TEXT")
    private String comment;
}

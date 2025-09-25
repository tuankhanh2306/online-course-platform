package edu.uth.online_course_platform.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "lessons")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lesson_id")
    private Long lesson_id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "lesson_title", nullable = false)
    private String lessonTitle;

    @Column(name = "drive_link", nullable = false, length = 500)
    private String driveLink; // link Google Drive

    @Column(name = "lesson_order", nullable = false)
    private Integer lessonOrder; // Thứ tự buổi học

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

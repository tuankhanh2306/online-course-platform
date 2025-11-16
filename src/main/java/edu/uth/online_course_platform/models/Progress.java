package edu.uth.online_course_platform.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Progress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long progressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User student; // Người học

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson; // Bài học đã hoàn thành

    @Column(name = "completion_date")
    private LocalDateTime completionDate; // Thời gian hoàn thành

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted; // Đã hoàn thành hay chưa
}
package edu.uth.online_course_platform.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lessons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long lessonId;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    @JsonBackReference
    private Course course;

    @Column(nullable = false, length = 255)
    private String title; // Tên bài học

    @Column(columnDefinition = "TEXT")
    private String description; // Mô tả ngắn

    @Column(name = "drive_link", nullable = false, length = 500)
    private String driveLink; // Đường dẫn file/video


    @Column(name = "order_index") // Tên cột trong DB
    private int orderIndex; // Thứ tự trong khóa học

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false)
    private java.time.LocalDateTime createdAt;


}

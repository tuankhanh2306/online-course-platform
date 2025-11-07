package edu.uth.online_course_platform.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    // Enum định nghĩa các trạng thái của khóa học
    public enum CourseStatus {
        DRAFT,            // Bản nháp, chỉ giảng viên thấy
        PENDING_APPROVAL, // Chờ duyệt, admin thấy
        PUBLISHED,        // Đã xuất bản, mọi người thấy
        REJECTED          // Bị từ chối, giảng viên thấy
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id", columnDefinition = "BIGINT UNSIGNED")
    private Long courseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", nullable = false)
    @JsonBackReference
    private User instructor;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private CourseStatus status = CourseStatus.DRAFT; // Trạng thái mặc định khi tạo mới

    @Column(name = "image_url", length = 500) // Cho phép URL dài
    private String imageUrl;

    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Lesson> lessons;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("course-enrollments")
    private List<Enrollment> enrollments;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Feedback> feedbacks;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
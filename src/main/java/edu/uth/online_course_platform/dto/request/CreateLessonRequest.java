package edu.uth.online_course_platform.dto.request;

import edu.uth.online_course_platform.models.Course;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateLessonRequest {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 500)
    private String driveLink;

    @NotNull(message = "Course Order index cannot be null")
    @DecimalMin(value = "0", inclusive = false, message = "Lesson order index must be greater than 0")
    private int orderIndex = 0;
}

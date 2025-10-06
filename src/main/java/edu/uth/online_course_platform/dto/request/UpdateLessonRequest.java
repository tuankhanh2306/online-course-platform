package edu.uth.online_course_platform.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateLessonRequest {
    @NotBlank(message = "Course's Title cannot be blank!")
    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Course's Drive Link cannot be blank!")
    @Column(nullable = false, length = 500)
    private String driveLink;

    @NotBlank(message = "Course's order index cannot be blank!")
    @Column(nullable = false)
    private int orderIndex = 0;
}

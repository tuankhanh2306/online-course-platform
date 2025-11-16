package edu.uth.online_course_platform.dto.request;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateLessonRequest {
    @Column(nullable = false, length = 200)
    private String title;

    private String description;

    @Column(nullable = false, length = 500)
    private String driveLink;

    @Column(nullable = false)
    private Integer orderIndex = 0;
}

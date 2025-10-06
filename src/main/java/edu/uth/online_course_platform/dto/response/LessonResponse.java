package edu.uth.online_course_platform.dto.response;

import edu.uth.online_course_platform.models.Course;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LessonResponse {
    private String courseTitle;

    private String title;

    private String description;

    private String driveLink;

    private int orderIndex;
}

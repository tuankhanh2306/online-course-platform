package edu.uth.online_course_platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LessonResponse {
    private String courseTitle;

    private Long lessonId;

    private String title;

    private String description;

    private String driveLink;

    private int orderIndex;
}

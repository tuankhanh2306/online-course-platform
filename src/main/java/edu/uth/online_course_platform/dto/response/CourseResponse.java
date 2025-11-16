package edu.uth.online_course_platform.dto.response;

import edu.uth.online_course_platform.models.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CourseResponse {

    private Long courseId;

    private String instructorName;

    private String title;

    private String description;

    private BigDecimal price;

    private String imageUrl;

    private Course.CourseStatus status;


    private int lessonCount;
    private long enrollmentCount;
    private BigDecimal courseRevenue;
}

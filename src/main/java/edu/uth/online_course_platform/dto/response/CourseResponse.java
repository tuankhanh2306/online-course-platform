package edu.uth.online_course_platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class CourseResponse {

    private Long courseId;

    private String instructorName;

    private String title;

    private String description;

    private BigDecimal price;

}

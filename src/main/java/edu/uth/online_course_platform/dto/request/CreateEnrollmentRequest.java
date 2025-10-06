package edu.uth.online_course_platform.dto.request;

import edu.uth.online_course_platform.models.Course;
import edu.uth.online_course_platform.models.Payment;
import edu.uth.online_course_platform.models.User;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CreateEnrollmentRequest {
    private Long courseId;
}

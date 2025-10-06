package edu.uth.online_course_platform.dto.request;

import edu.uth.online_course_platform.models.Enrollment;
import edu.uth.online_course_platform.models.Feedback;
import edu.uth.online_course_platform.models.Lesson;
import edu.uth.online_course_platform.models.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CreateCourseRequest {

    @NotBlank(message = "Title of course cannot be blank")
    @Size(max = 255)
    private String title;

    @NotBlank(message = "Description of course cannot be blank")
    private String description;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

}

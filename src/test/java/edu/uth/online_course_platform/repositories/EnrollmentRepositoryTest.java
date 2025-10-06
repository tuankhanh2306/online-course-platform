package edu.uth.online_course_platform.repositories;

import edu.uth.online_course_platform.models.Course;
import edu.uth.online_course_platform.models.Enrollment;
import edu.uth.online_course_platform.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest  // Dùng database H2, chỉ load phần JPA
@EntityScan(basePackages = "edu.uth.online_course_platform.models")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EnrollmentRepositoryTest {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    private User student;
    private Course course;

    @BeforeEach
    void setup() {
        // Tạo User (student)
        student = User.builder()
                .fullName("Nguyen Van A")
                .email("a@example.com")
                .password("123456")
                .role(User.UserRole.STUDENT)
                .build();
        student = userRepository.save(student);

        // Tạo Course
        course = Course.builder()
                .title("Java Basics")
                .description("Learn Java from scratch")
                .price(new java.math.BigDecimal("99.99"))
                .build();
        course = courseRepository.save(course);

        // Tạo Enrollment
        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .status(Enrollment.EnrollmentStatus.ACTIVE)
                .build();
        enrollmentRepository.save(enrollment);
    }

    @Test
    void testFindByStudent_UserId() {
        List<Enrollment> result = enrollmentRepository.findByStudent_UserId(student.getUserId());

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getCourse().getTitle()).isEqualTo("Java Basics");
    }

    @Test
    void testFindByStudent_UserIdAndCourse_CourseId() {
        Optional<Enrollment> result =
                enrollmentRepository.findByStudent_UserIdAndCourse_CourseId(student.getUserId(), course.getCourseId());

        assertThat(result).isPresent();
        assertThat(result.get().getStatus()).isEqualTo(Enrollment.EnrollmentStatus.ACTIVE);
    }
}

package edu.uth.online_course_platform.repositories;

import edu.uth.online_course_platform.models.Enrollment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends CrudRepository<Enrollment, Long> {
    Optional<Enrollment> findByStudent_UserIdAndCourse_CourseId(Long studentId, Long courseId);
    List<Enrollment> findByStudent_UserId(Long studentId);

}

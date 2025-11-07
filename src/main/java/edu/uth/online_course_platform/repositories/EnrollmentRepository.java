package edu.uth.online_course_platform.repositories;

import edu.uth.online_course_platform.models.Course;
import edu.uth.online_course_platform.models.Enrollment;
import edu.uth.online_course_platform.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends CrudRepository<Enrollment, Long> {
    Optional<Enrollment> findByStudent_UserIdAndCourse_CourseId(Long studentId, Long courseId);
    List<Enrollment> findByStudent_UserId(Long studentId);

    Optional<Object> findByStudentAndCourse(User student, Course course);

    boolean existsByStudentAndCourse(User student, Course course);

    List<Enrollment> findByStudent(User student);
}

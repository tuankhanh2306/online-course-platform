package edu.uth.online_course_platform.repositories;

import edu.uth.online_course_platform.models.Course;
import edu.uth.online_course_platform.models.Lesson;
import edu.uth.online_course_platform.models.Progress;
import edu.uth.online_course_platform.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {
    // Tìm tiến độ của một học viên cho một bài học cụ thể
    Optional<Progress> findByStudentAndLesson(User student, Lesson lesson);

    // Tìm tất cả tiến độ bài học của một học viên cho một khóa học cụ thể
    // (Cần một JOIN qua Lesson để đến Course)
    List<Progress> findByStudentAndLesson_Course(User student, Course course);

    // Kiểm tra xem một bài học đã được hoàn thành bởi một học viên chưa
    boolean existsByStudentAndLessonAndIsCompleted(User student, Lesson lesson, boolean isCompleted);
}
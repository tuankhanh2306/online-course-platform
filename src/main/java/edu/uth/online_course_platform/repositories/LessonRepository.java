package edu.uth.online_course_platform.repositories;

import edu.uth.online_course_platform.models.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson,Long> {
    List<Lesson> findByCourse_CourseId(Long courseId);

    List<Lesson> findByCourse_CourseIdOrderByOrderIndexAsc(Long courseId);

    long countByCourse_CourseId(Long courseId);
}

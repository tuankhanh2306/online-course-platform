package edu.uth.online_course_platform.repositories;

import edu.uth.online_course_platform.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Lấy danh sách các khóa học theo một trạng thái cụ thể
    List<Course> findByStatus(Course.CourseStatus status);

    // Lấy một khóa học theo ID và trạng thái (dùng cho API public)
    // SỬA LỖI Ở ĐÂY: findByIdAndStatus -> findByCourseIdAndStatus
    Optional<Course> findByCourseIdAndStatus(Long courseId, Course.CourseStatus status);
}
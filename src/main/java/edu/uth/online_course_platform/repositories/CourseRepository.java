package edu.uth.online_course_platform.repositories;

import edu.uth.online_course_platform.models.Course;
import edu.uth.online_course_platform.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    List<Course> findByInstructor(User instructor);

    /**
     * THÊM PHƯƠNG THỨC NÀY
     * Lấy tổng số bài học của mỗi khóa học cho một giảng viên.
     * Trả về List<Object[]> với mỗi Object[] là [courseId (Long), count (Long)]
     */
    @Query("SELECT c.courseId, COUNT(l.lessonId) FROM Course c " +
            "LEFT JOIN c.lessons l " +
            "WHERE c.instructor = :instructor " +
            "GROUP BY c.courseId")
    List<Object[]> findLessonCountsByInstructor(@Param("instructor") User instructor);
    long countByInstructor(User instructor);
}
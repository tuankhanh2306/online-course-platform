package edu.uth.online_course_platform.until;

import edu.uth.online_course_platform.dto.request.LoginRequest;
import edu.uth.online_course_platform.dto.response.CourseResponse;
import edu.uth.online_course_platform.dto.response.LessonResponse;
import edu.uth.online_course_platform.dto.response.LoginResponse;
import edu.uth.online_course_platform.models.Course;
import edu.uth.online_course_platform.models.Lesson;
import org.springframework.stereotype.Component;

@Component
public class Mapper {
    public LessonResponse transformToLessonResponse(Lesson lesson) {
        return  new LessonResponse(
                lesson.getCourse().getTitle(),
                lesson.getTitle(),
                lesson.getDescription(),
                lesson.getDriveLink(),
                lesson.getOrderIndex()
        );
    }

    public CourseResponse transformCourseToCourseResponse(Course course) {
        return  new CourseResponse(
                course.getCourseId(),
                course.getInstructor().getFullName(),
                course.getTitle(),
                course.getDescription(),
                course.getPrice());
    }


}

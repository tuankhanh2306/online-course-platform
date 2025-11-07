package edu.uth.online_course_platform.until;

import edu.uth.online_course_platform.dto.response.CourseResponse;
import edu.uth.online_course_platform.dto.response.LessonResponse;
import edu.uth.online_course_platform.models.Course;
import edu.uth.online_course_platform.models.Lesson;
import org.springframework.stereotype.Component;

@Component
public class Mapper {

    public LessonResponse transformToLessonResponse(Lesson lesson) {
        return LessonResponse.builder() // Nên dùng builder
                .lessonId(lesson.getLessonId())
                .title(lesson.getTitle())
                .description(lesson.getDescription())
                .driveLink(lesson.getDriveLink())
                .orderIndex(lesson.getOrderIndex())
                .build();
    }

    public CourseResponse transformCourseToCourseResponse(Course course) {
        return CourseResponse.builder() // Sử dụng builder
                .courseId(course.getCourseId())
                .instructorName(course.getInstructor() != null ? course.getInstructor().getFullName() : "N/A")
                .title(course.getTitle())
                .description(course.getDescription())
                .price(course.getPrice())
                .imageUrl(course.getImageUrl())
                .build();
    }
}
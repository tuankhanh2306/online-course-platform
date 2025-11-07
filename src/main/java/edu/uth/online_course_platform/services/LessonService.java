package edu.uth.online_course_platform.services;

import edu.uth.online_course_platform.dto.request.CreateLessonRequest;
import edu.uth.online_course_platform.dto.request.UpdateLessonRequest;
import edu.uth.online_course_platform.dto.response.LessonResponse;
import edu.uth.online_course_platform.exceptions.AppException;
import edu.uth.online_course_platform.exceptions.ErrorCode;
import edu.uth.online_course_platform.exceptions.ResourceNotFoundException;
import edu.uth.online_course_platform.models.Course;
import edu.uth.online_course_platform.models.Lesson;
import edu.uth.online_course_platform.models.User;
import edu.uth.online_course_platform.repositories.CourseRepository;
import edu.uth.online_course_platform.repositories.EnrollmentRepository;
import edu.uth.online_course_platform.repositories.LessonRepository;
import edu.uth.online_course_platform.until.AuthorizationService;
import edu.uth.online_course_platform.until.Mapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LessonService {
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private Mapper mapper;
    @Autowired
    private  EnrollmentRepository enrollmentRepository;
    @Autowired
    private  CourseRepository courseRepository;

    public LessonResponse createNewLesson (Course course, CreateLessonRequest request) throws IllegalAccessException{
        if(!authorizationService.isOwnerVerified(course)) throw new IllegalAccessException("Your access to this course is illegal");

        Lesson lesson = new Lesson();
        lesson.setCourse(course);
        lesson.setLessonId(null);
        lesson.setTitle(request.getTitle());
        lesson.setDescription(request.getDescription());
        lesson.setDriveLink(request.getDriveLink());
        lesson.setOrderIndex(request.getOrderIndex());
        return mapper.transformToLessonResponse(lessonRepository.save(lesson));
    }

    public LessonResponse updateLesson (Long lessonId, UpdateLessonRequest request) throws IllegalAccessException{
        Lesson lesson = lessonRepository.findById(lessonId).get();
        Course course = lesson.getCourse();
        if(!authorizationService.isOwnerVerified(course)) throw new IllegalAccessException("Your access to this course is illegal");

        if(!request.getTitle().isEmpty()){
            lesson.setTitle(request.getTitle());
        }
        if(!request.getDescription().isEmpty()){
            lesson.setDescription(request.getDescription());
        }
        if(!request.getDriveLink().isEmpty()){
            lesson.setDriveLink(request.getDriveLink());
        }
        lesson.setOrderIndex(request.getOrderIndex());

        return mapper.transformToLessonResponse(lessonRepository.save(lesson));
    }

    public void deleteLesson (Long lessonId) throws IllegalAccessException{
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
        Course course = lesson.getCourse();
        if(!authorizationService.isOwnerVerified(course)) throw new IllegalAccessException("Your access to this course is illegal");
        lessonRepository.deleteById(lessonId);
    }

    @Transactional(readOnly = true)
    public List<LessonResponse> getLessonsForEnrolledStudent(Long courseId) {
        // 1. Lấy thông tin người dùng (học viên) hiện tại
        User student = authorizationService.getCurrentUser();

        // 2. Kiểm tra xem khóa học có tồn tại không
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + courseId));

        // 3. KIỂM TRA QUAN TRỌNG: Học viên này đã đăng ký khóa học chưa?
        boolean isEnrolled = enrollmentRepository.existsByStudentAndCourse(student, course);
        if (!isEnrolled) {
            // Nếu chưa đăng ký, ném lỗi 403 Forbidden
            throw new AppException(ErrorCode.NOT_ENROLLED);
        }

        // 4. Nếu đã đăng ký, lấy danh sách bài học
        List<Lesson> lessons = lessonRepository.findByCourse_CourseIdOrderByOrderIndexAsc(courseId);

        // 5. Chuyển đổi (map) từ Lesson (Entity) sang LessonResponse (DTO)
        return lessons.stream()
                .map(lesson -> LessonResponse.builder()
                        .lessonId(lesson.getLessonId())
                        .title(lesson.getTitle())
                        .description(lesson.getDescription())
                        .driveLink(lesson.getDriveLink())
                        .orderIndex(lesson.getOrderIndex())
                        .build())
                .collect(Collectors.toList());
    }
}

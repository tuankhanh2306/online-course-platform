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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final AuthorizationService authorizationService;
    private final Mapper mapper;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public LessonResponse createNewLesson(Course course, CreateLessonRequest request) throws IllegalAccessException {
        // Kiểm tra quyền sở hữu khóa học trước khi tạo bài học
        if (!authorizationService.isOwnerVerified(course)) {
            throw new IllegalAccessException("Your access to this course is illegal");
        }

        Lesson lesson = new Lesson();
        lesson.setCourse(course);
        lesson.setTitle(request.getTitle());
        lesson.setDescription(request.getDescription());
        lesson.setDriveLink(request.getDriveLink());
        lesson.setOrderIndex(request.getOrderIndex());
        return mapper.transformToLessonResponse(lessonRepository.save(lesson));
    }

    // Lấy chi tiết bài học cho giảng viên (bao gồm kiểm tra quyền sở hữu) - Bổ sung nghiệp vụ
    @Transactional(readOnly = true)
    public LessonResponse getInstructorLessonDetails(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));
        Course course = lesson.getCourse();

        if (!authorizationService.isOwnerVerified(course)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return mapper.transformToLessonResponse(lesson);
    }

    @Transactional
    public LessonResponse updateLesson(Long lessonId, UpdateLessonRequest request) throws IllegalAccessException {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));

        Course course = lesson.getCourse();
        if (!authorizationService.isOwnerVerified(course)) {
            throw new IllegalAccessException("Your access to this course is illegal");
        }

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            lesson.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            lesson.setDescription(request.getDescription());
        }
        if (request.getDriveLink() != null && !request.getDriveLink().isBlank()) {
            lesson.setDriveLink(request.getDriveLink());
        }
        if (request.getOrderIndex() != null) { // Chỉ cập nhật nếu có gửi lên
            lesson.setOrderIndex(request.getOrderIndex());
        }

        return mapper.transformToLessonResponse(lessonRepository.save(lesson));
    }

    @Transactional
    public void deleteLesson(Long lessonId) throws IllegalAccessException {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
        Course course = lesson.getCourse();
        if (!authorizationService.isOwnerVerified(course)) {
            throw new IllegalAccessException("Your access to this course is illegal");
        }
        lessonRepository.delete(lesson);
    }

    // Lấy danh sách bài học theo Course ID (dành cho CourseService gọi, không kiểm tra đăng ký)
    @Transactional(readOnly = true)
    public List<Lesson> getLessonsByCourse(Long courseId) {
        return lessonRepository.findByCourse_CourseIdOrderByOrderIndexAsc(courseId);
    }

    // Get lessons for an enrolled student (bao gồm kiểm tra đăng ký)
    @Transactional(readOnly = true)
    public List<LessonResponse> getLessonsForEnrolledStudent(Long courseId) {
        User student = authorizationService.getCurrentUser();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + courseId));

        boolean isEnrolled = enrollmentRepository.existsByStudentAndCourse(student, course);
        if (!isEnrolled) {
            throw new AppException(ErrorCode.NOT_ENROLLED);
        }

        List<Lesson> lessons = lessonRepository.findByCourse_CourseIdOrderByOrderIndexAsc(courseId);
        return lessons.stream()
                .map(mapper::transformToLessonResponse)
                .collect(Collectors.toList());
    }
}
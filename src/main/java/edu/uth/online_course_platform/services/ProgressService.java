package edu.uth.online_course_platform.services;

import edu.uth.online_course_platform.exceptions.AppException;
import edu.uth.online_course_platform.exceptions.ErrorCode;
import edu.uth.online_course_platform.exceptions.ResourceNotFoundException;
import edu.uth.online_course_platform.models.Course;
import edu.uth.online_course_platform.models.Lesson;
import edu.uth.online_course_platform.models.Progress;
import edu.uth.online_course_platform.models.User;
import edu.uth.online_course_platform.repositories.LessonRepository;
import edu.uth.online_course_platform.repositories.ProgressRepository;
import edu.uth.online_course_platform.until.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProgressService {

    @Autowired
    private ProgressRepository progressRepository;
    @Autowired
    private LessonRepository lessonRepository; // Cần để lấy Lesson
    @Autowired
    private AuthorizationService authorizationService; // Để lấy User hiện tại
    @Autowired
    private EnrollmentService enrollmentService; // Để kiểm tra học viên đã đăng ký khóa học chưa

    /**
     * Đánh dấu một bài học là đã hoàn thành.
     * Kiểm tra xem người dùng có phải là học viên của khóa học không.
     */
    @Transactional
    public Progress completeLesson(Long lessonId) {
        User currentUser = authorizationService.getCurrentUser();
        if (currentUser == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED); // Hoặc UNAUTHORIZED tùy cách xử lý
        }

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Bài học không tìm thấy với ID: " + lessonId));

        // Kiểm tra xem học viên đã đăng ký khóa học này chưa
        boolean hasEnrolled = enrollmentService.isStudentEnrolledInCourse(currentUser.getUserId(), lesson.getCourse().getCourseId());
        if (!hasEnrolled) {
            throw new AppException(ErrorCode.UNAUTHORIZED); // Học viên chưa đăng ký khóa học
        }

        Optional<Progress> existingProgress = progressRepository.findByStudentAndLesson(currentUser, lesson);

        Progress progress;
        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
            if (!progress.isCompleted()) { // Chỉ cập nhật nếu chưa hoàn thành
                progress.setCompleted(true);
                progress.setCompletionDate(LocalDateTime.now());
            }
        } else {
            // Tạo tiến độ mới nếu chưa có
            progress = Progress.builder()
                    .student(currentUser)
                    .lesson(lesson)
                    .isCompleted(true)
                    .completionDate(LocalDateTime.now())
                    .build();
        }
        return progressRepository.save(progress);
    }

    /**
     * Lấy danh sách các Lesson ID mà học viên đã hoàn thành trong một khóa học cụ thể.
     * Sử dụng để cập nhật giao diện khi tải trang.
     */
    @Transactional(readOnly = true)
    public List<Long> getCompletedLessonIdsForCourse(Long courseId) {
        User currentUser = authorizationService.getCurrentUser();
        if (currentUser == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Kiểm tra xem học viên đã đăng ký khóa học này chưa
        boolean hasEnrolled = enrollmentService.isStudentEnrolledInCourse(currentUser.getUserId(), courseId);
        if (!hasEnrolled) {
            // Không ném lỗi UNAUTHORIZED ở đây, chỉ trả về danh sách trống hoặc null
            // vì có thể frontend chỉ muốn kiểm tra các bài đã hoàn thành nếu họ có quyền truy cập
            return List.of();
        }

        // Lấy Course object (có thể cần CourseRepository)
        Course course = lessonRepository.findById(courseId) // Giả sử lessonRepository có method findByCourseId
                .map(Lesson::getCourse)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));


        return progressRepository.findByStudentAndLesson_Course(currentUser, course)
                .stream()
                .filter(Progress::isCompleted)
                .map(p -> p.getLesson().getLessonId())
                .collect(Collectors.toList());
    }

    /**
     * Kiểm tra xem một bài học cụ thể đã được hoàn thành bởi người dùng hiện tại chưa.
     */
    @Transactional(readOnly = true)
    public boolean isLessonCompletedByUser(Long lessonId, Long userId) {
        User user = authorizationService.getCurrentUser(); // Hoặc lấy từ userId truyền vào
        if (user == null || !user.getUserId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Bài học không tìm thấy với ID: " + lessonId));
        return progressRepository.existsByStudentAndLessonAndIsCompleted(user, lesson, true);
    }
}
package edu.uth.online_course_platform.services;

import edu.uth.online_course_platform.dto.request.CreateCourseRequest;
import edu.uth.online_course_platform.dto.request.CreateLessonRequest;
import edu.uth.online_course_platform.dto.request.UpdateCourseRequest;
import edu.uth.online_course_platform.dto.response.CourseResponse;
import edu.uth.online_course_platform.dto.response.InstructorRevenueResponse;
import edu.uth.online_course_platform.dto.response.LessonResponse;
import edu.uth.online_course_platform.exceptions.AppException;
import edu.uth.online_course_platform.exceptions.ErrorCode;
import edu.uth.online_course_platform.exceptions.ResourceNotFoundException;
import edu.uth.online_course_platform.models.Course;
import edu.uth.online_course_platform.models.Lesson;
import edu.uth.online_course_platform.models.User;
import edu.uth.online_course_platform.repositories.CourseRepository;
import edu.uth.online_course_platform.repositories.PaymentRepository;
import edu.uth.online_course_platform.until.AuthorizationService;
import edu.uth.online_course_platform.until.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final PaymentRepository paymentRepository;
    private final LessonService lessonService; // Inject LessonService
    private final AuthorizationService authorizationService;
    private final Mapper mapper;

    // Create new Course
    @Transactional
    public CourseResponse createNewCourse(CreateCourseRequest createCourseRequest) {
        Course course = new Course();
        User instructor = authorizationService.getCurrentUser();
        course.setInstructor(instructor);
        course.setTitle(createCourseRequest.getTitle());
        course.setDescription(createCourseRequest.getDescription());
        course.setPrice(createCourseRequest.getPrice());
        course.setImageUrl(createCourseRequest.getImageUrl());
        course.setStatus(Course.CourseStatus.DRAFT); // Khóa học mới tạo luôn ở trạng thái DRAFT
        return mapper.transformCourseToCourseResponse(courseRepository.save(course));
    }

    // Get All courses of current instructor:
    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourseOfInstructor() {
        User instructor = authorizationService.getCurrentUser();

        // 1. Lấy danh sách khóa học CƠ BẢN
        List<Course> courses = courseRepository.findByInstructor(instructor);

        // 2. Lấy tất cả thống kê trong 3 câu query hiệu quả
        Map<Long, BigDecimal> revenueMap = paymentRepository.findCourseRevenuesByInstructor(instructor)
                .stream()
                .collect(Collectors.toMap(
                        obj -> (Long) obj[0],
                        obj -> (BigDecimal) obj[1]
                ));

        Map<Long, Long> enrollmentMap = paymentRepository.findCourseEnrollmentCountsByInstructor(instructor)
                .stream()
                .collect(Collectors.toMap(
                        obj -> (Long) obj[0],
                        obj -> (Long) obj[1]
                ));

        Map<Long, Long> lessonMap = courseRepository.findLessonCountsByInstructor(instructor)
                .stream()
                .collect(Collectors.toMap(
                        obj -> (Long) obj[0],
                        obj -> (Long) obj[1]
                ));

        // 3. Map Course sang CourseResponse và điền các thống kê
        return courses.stream()
                .map(course -> {
                    // 3.1. Map các trường cơ bản (title, status, v.v.)
                    CourseResponse response = mapper.transformCourseToCourseResponse(course);

                    // 3.2. Lấy thống kê từ Map, mặc định là 0 nếu không tìm thấy
                    BigDecimal courseRevenue = revenueMap.getOrDefault(course.getCourseId(), BigDecimal.ZERO);
                    long enrollmentCount = enrollmentMap.getOrDefault(course.getCourseId(), 0L);
                    int lessonCount = lessonMap.getOrDefault(course.getCourseId(), 0L).intValue();

                    // 3.3. Set các giá trị thống kê vào DTO
                    response.setCourseRevenue(courseRevenue);
                    response.setEnrollmentCount(enrollmentCount);
                    response.setLessonCount(lessonCount);

                    return response;
                })
                .collect(Collectors.toList());
    }

    // Lấy chi tiết một khóa học cụ thể của giảng viên (cho trang edit_course) - Nghiệp vụ đã bổ sung
    @Transactional(readOnly = true)
    public CourseResponse getInstructorCourseDetails(Long courseId) {
        User instructor = authorizationService.getCurrentUser();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + courseId));

        if (!course.getInstructor().getUserId().equals(instructor.getUserId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return mapper.transformCourseToCourseResponse(course);
    }

    // Update instructor's course by ID - Nghiệp vụ đã có
    @Transactional
    public CourseResponse updateCourse(Long courseId, UpdateCourseRequest request) {
        User instructor = authorizationService.getCurrentUser();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + courseId));

        if (!course.getInstructor().getUserId().equals(instructor.getUserId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            course.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            course.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            course.setPrice(request.getPrice());
        }
        if (request.getImageUrl() != null) {
            course.setImageUrl(request.getImageUrl());
        }

        Course updatedCourse = courseRepository.save(course);
        return mapper.transformCourseToCourseResponse(updatedCourse);
    }

    // Submit course for approval - Nghiệp vụ đã có
    @Transactional
    public void submitCourse(Long courseId) {
        User instructor = authorizationService.getCurrentUser();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        if (!course.getInstructor().getUserId().equals(instructor.getUserId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        course.setStatus(Course.CourseStatus.PENDING_APPROVAL);
        courseRepository.save(course);
    }

    // Get instructor's revenue dashboard - Nghiệp vụ đã có
    @Transactional(readOnly = true)
    public InstructorRevenueResponse getInstructorRevenueDashboard() {
        User instructor = authorizationService.getCurrentUser();

        // 1. Lấy doanh thu (đã sửa lỗi 'COMPLETED')
        BigDecimal totalRevenue = paymentRepository.findTotalRevenueByInstructor(instructor);

        // 2. Lấy số lượt đăng ký (đã sửa lỗi 'COMPLETED')
        long totalEnrollments = paymentRepository.countSuccessfulEnrollmentsByInstructor(instructor);

        // 3. THÊM DÒNG NÀY: Lấy tổng số khóa học
        long totalCourses = courseRepository.countByInstructor(instructor);

        // 4. CẬP NHẬT TRÌNH BUILDER
        return InstructorRevenueResponse.builder()
                .totalEnrollments(totalEnrollments)
                .totalRevenue(totalRevenue)
                .totalCourses(totalCourses) // <-- Thêm trường này
                .build();
    }

    // Lấy danh sách các bài học của một khóa học (thuộc sở hữu của giảng viên) - Nghiệp vụ đã có
    @Transactional(readOnly = true)
    public List<LessonResponse> getListLessonsByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        if (!authorizationService.isOwnerVerified(course)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        List<Lesson> lessons = lessonService.getLessonsByCourse(courseId); // Gọi LessonService để lấy list Lessons
        return lessons.stream()
                .map(mapper::transformToLessonResponse)
                .collect(Collectors.toList());
    }

    // Create a new lesson in a course: - Nghiệp vụ đã có
    @Transactional
    public LessonResponse createNewLesson(Long courseId, CreateLessonRequest createLessonRequest) throws IllegalAccessException, ResourceNotFoundException {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        if (!authorizationService.isOwnerVerified(course)) {
            throw new IllegalAccessException("Bạn không có quyền sửa đổi khóa học này.");
        }

        return lessonService.createNewLesson(course, createLessonRequest);
    }

    // Các nghiệp vụ Admin (nếu có, không sửa):
    @Transactional(readOnly = true)
    public List<CourseResponse> getPendingApprovalCourses() {
        return courseRepository.findByStatus(Course.CourseStatus.PENDING_APPROVAL)
                .stream()
                .map(mapper::transformCourseToCourseResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void approveCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        course.setStatus(Course.CourseStatus.PUBLISHED);
        courseRepository.save(course);
    }

    @Transactional
    public void rejectCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        course.setStatus(Course.CourseStatus.REJECTED);
        courseRepository.save(course);
    }

    // Các nghiệp vụ Public (nếu có, không sửa):
    @Transactional(readOnly = true)
    public List<CourseResponse> getPublishedCourses() {
        return courseRepository.findByStatus(Course.CourseStatus.PUBLISHED)
                .stream()
                .map(mapper::transformCourseToCourseResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CourseResponse getPublishedCourseById(Long courseId) {
        Course course = courseRepository.findByCourseIdAndStatus(courseId, Course.CourseStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Published course not found"));
        return mapper.transformCourseToCourseResponse(course);
    }
}
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private LessonService lessonService;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private Mapper mapper;

    // Create new Course
    public CourseResponse createNewCourse(CreateCourseRequest createCourseRequest) {
        Course course = new Course();
        User instructor = authorizationService.getCurrentUser();
        course.setInstructor(instructor);
        course.setTitle(createCourseRequest.getTitle());
        course.setDescription(createCourseRequest.getDescription());
        course.setPrice(createCourseRequest.getPrice());
        course.setImageUrl(createCourseRequest.getImageUrl());
        return mapper.transformCourseToCourseResponse(courseRepository.save(course));
    }

    // Get All course of current instructor:
    @Transactional(readOnly = true)
    public List<Course> getAllCourseOfInstructor() {
        User instructor = authorizationService.getCurrentUser();
        return instructor.getCourses();
    }

    public List<Course> getListCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course not found"));
    }

    public List<Lesson> getListLessonsByCourse(Long id) {
        Course course = courseRepository.findById(id).orElseThrow(() ->new ResourceNotFoundException("Course not found"));
        return course.getLessons();
    }

    public LessonResponse createNewLesson (Long courseId, CreateLessonRequest createLessonRequest) throws IllegalAccessException, ResourceNotFoundException {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        return lessonService.createNewLesson(course, createLessonRequest);
    }

    //  Logic cho Giảng viên gửi duyệt khóa học
    @Transactional
    public void submitCourse(Long courseId) {
        User instructor = authorizationService.getCurrentUser();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        if (!course.getInstructor().getUserId().equals(instructor.getUserId())) {
            throw new RuntimeException("You are not the owner of this course.");
        }

        course.setStatus(Course.CourseStatus.PENDING_APPROVAL);
        courseRepository.save(course);
    }

    // Logic cho Admin lấy danh sách các khóa học đang chờ duyệt
    @Transactional(readOnly = true)
    public List<CourseResponse> getPendingApprovalCourses() {
        return courseRepository.findByStatus(Course.CourseStatus.PENDING_APPROVAL)
                .stream()
                .map(mapper::transformCourseToCourseResponse)
                .collect(Collectors.toList());
    }

    //  Logic cho Admin phê duyệt khóa học
    @Transactional
    public void approveCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        course.setStatus(Course.CourseStatus.PUBLISHED);
        courseRepository.save(course);
    }

    // Logic cho Admin từ chối khóa học
    @Transactional
    public void rejectCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        course.setStatus(Course.CourseStatus.REJECTED);
        courseRepository.save(course);
    }

    // dành cho user k cần đăng nhập
    public List<CourseResponse> getPublishedCourses() {
        return courseRepository.findByStatus(Course.CourseStatus.PUBLISHED)
                .stream()
                .map(mapper::transformCourseToCourseResponse)
                .collect(Collectors.toList());
    }

    public CourseResponse getPublishedCourseById(Long courseId) {
        Course course = courseRepository.findByCourseIdAndStatus(courseId, Course.CourseStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Published course not found"));
        return mapper.transformCourseToCourseResponse(course);
    }

    public InstructorRevenueResponse getInstructorRevenueDashboard() {
        // 1. Lấy thông tin Giảng viên hiện tại
        User instructor = authorizationService.getCurrentUser();

        // 2. Gọi các phương thức repository để lấy số liệu
        BigDecimal totalRevenue = paymentRepository.findTotalRevenueByInstructor(instructor);
        long totalEnrollments = paymentRepository.countSuccessfulEnrollmentsByInstructor(instructor);

        // 3. Xây dựng và trả về DTO
        return InstructorRevenueResponse.builder()
                .totalEnrollments(totalEnrollments)
                .totalRevenue(totalRevenue)
                .build();
    }

    /**
     * Cho phép Giảng viên cập nhật thông tin khóa học của chính họ.
     */
    @Transactional
    public CourseResponse updateCourse(Long courseId, UpdateCourseRequest request) {
        // 1. Lấy thông tin Giảng viên và khóa học
        User instructor = authorizationService.getCurrentUser();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + courseId));

        // 2. KIỂM TRA QUYỀN SỞ HỮU: Giảng viên này có phải là người tạo khóa học không?
        if (!course.getInstructor().getUserId().equals(instructor.getUserId())) {
            // Nếu không phải, ném lỗi 403 Forbidden
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // 3. Cập nhật các trường nếu chúng được cung cấp trong request
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

        // Khi cập nhật, có thể nên đặt lại trạng thái về DRAFT để Admin duyệt lại
        course.setStatus(Course.CourseStatus.DRAFT);

        // 4. Lưu lại
        Course updatedCourse = courseRepository.save(course);

        // 5. Trả về DTO
        return mapper.transformCourseToCourseResponse(updatedCourse);
    }
    /*
    * 1. Nguoi dung vao khoa hoc da co -> co san Id cua khoa hoc
    * 2. Nguoi dung chon create new lesson -> thuc hien tao lesson truoc, sau do moi lay lesson vua tao them vao danh sach lesson trong course do
    * */
}

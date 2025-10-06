package edu.uth.online_course_platform.services;

import edu.uth.online_course_platform.dto.request.CreateCourseRequest;
import edu.uth.online_course_platform.dto.request.CreateLessonRequest;
import edu.uth.online_course_platform.dto.response.CourseResponse;
import edu.uth.online_course_platform.dto.response.LessonResponse;
import edu.uth.online_course_platform.exceptions.ResourceNotFoundException;
import edu.uth.online_course_platform.models.Course;
import edu.uth.online_course_platform.models.Lesson;
import edu.uth.online_course_platform.models.User;
import edu.uth.online_course_platform.repositories.CourseRepository;
import edu.uth.online_course_platform.until.AuthorizationService;
import edu.uth.online_course_platform.until.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;
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
    /*
    * 1. Nguoi dung vao khoa hoc da co -> co san Id cua khoa hoc
    * 2. Nguoi dung chon create new lesson -> thuc hien tao lesson truoc, sau do moi lay lesson vua tao them vao danh sach lesson trong course do
    * */
}

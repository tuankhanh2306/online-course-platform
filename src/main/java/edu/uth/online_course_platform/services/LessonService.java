package edu.uth.online_course_platform.services;

import edu.uth.online_course_platform.dto.request.CreateLessonRequest;
import edu.uth.online_course_platform.dto.request.UpdateLessonRequest;
import edu.uth.online_course_platform.dto.response.LessonResponse;
import edu.uth.online_course_platform.exceptions.ResourceNotFoundException;
import edu.uth.online_course_platform.models.Course;
import edu.uth.online_course_platform.models.Lesson;
import edu.uth.online_course_platform.repositories.LessonRepository;
import edu.uth.online_course_platform.until.AuthorizationService;
import edu.uth.online_course_platform.until.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LessonService {
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private Mapper mapper;

    public LessonResponse createNewLesson (Course course, CreateLessonRequest request) throws IllegalAccessException{
        if(!authorizationService.isOwnerVerified(course)) throw new IllegalAccessException("Your access to this course is illegal");

        Lesson lesson = new Lesson();
        lesson.setCourse(course);
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
}

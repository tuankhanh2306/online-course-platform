package edu.uth.online_course_platform.until;

import edu.uth.online_course_platform.models.Course;
import edu.uth.online_course_platform.models.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    public User getCurrentUser () {
        return (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    // Authenticate Course's instructor or Admin?
    public boolean isOwnerVerified(Course course) {
        User currentUser = getCurrentUser();
        return course.getInstructor().equals(currentUser) || currentUser.getRole().equals(User.UserRole.ADMIN);
    }

    // Authenticate Student Role for enrolling:
    public User verifyCurrentStudentUser() throws IllegalAccessException{
        User currentUser = getCurrentUser();
        if (!currentUser.getRole().equals(User.UserRole.STUDENT)) {
            throw new IllegalAccessException("You are not student and cannot enrolling the course");
        }
        return currentUser;

    }
}

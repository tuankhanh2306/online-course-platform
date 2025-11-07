package edu.uth.online_course_platform.until;

import edu.uth.online_course_platform.exceptions.AppException;
import edu.uth.online_course_platform.exceptions.ErrorCode;
import edu.uth.online_course_platform.models.Course;
import edu.uth.online_course_platform.models.User;
import edu.uth.online_course_platform.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * AuthorizationService:
 * - Lấy thông tin user hiện tại từ SecurityContext
 * - Kiểm tra quyền sở hữu, quyền truy cập (Instructor, Student, Admin)
 */
@Service
public class AuthorizationService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Lấy entity User hiện tại từ Authentication (theo email trong token)
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Object principal = authentication.getPrincipal();
        String email;

        // Nếu principal là User mặc định của Spring Security
        if (principal instanceof org.springframework.security.core.userdetails.User springUser) {
            email = springUser.getUsername(); // getUsername() trả về email
        } else {
            // fallback cho trường hợp CustomUserDetails hoặc chuỗi "anonymousUser"
            email = principal.toString();
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * Kiểm tra xem user hiện tại có phải là instructor của course hoặc là admin không
     */
    public boolean isOwnerVerified(Course course) {
        User currentUser = getCurrentUser();

        // So sánh instructor ID thay vì equals() để tránh lỗi lazy proxy
        boolean isInstructor = course.getInstructor() != null &&
                course.getInstructor().getUserId().equals(currentUser.getUserId());

        boolean isAdmin = currentUser.getRole().equals(User.UserRole.ADMIN);

        return isInstructor || isAdmin;
    }

    /**
     * Kiểm tra user hiện tại có phải là STUDENT không — dùng khi enroll khóa học
     */
    public User verifyCurrentStudentUser() throws IllegalAccessException {
        User currentUser = getCurrentUser();

        if (!User.UserRole.STUDENT.equals(currentUser.getRole())) {
            throw new IllegalAccessException("You are not a student and cannot enroll in the course.");
        }

        return currentUser;
    }
}

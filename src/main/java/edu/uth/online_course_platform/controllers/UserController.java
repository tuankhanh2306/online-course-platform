package edu.uth.online_course_platform.controllers;

import edu.uth.online_course_platform.dto.request.ChangePasswordRequest;
import edu.uth.online_course_platform.dto.request.UserCreateDto;
import edu.uth.online_course_platform.dto.response.ApiResponse;
import edu.uth.online_course_platform.dto.response.EnrollmentResponse;
import edu.uth.online_course_platform.dto.response.UserResponseDto;
import edu.uth.online_course_platform.exceptions.AppException;
import edu.uth.online_course_platform.exceptions.ErrorCode;
import edu.uth.online_course_platform.models.User;
import edu.uth.online_course_platform.repositories.UserRepository;
import edu.uth.online_course_platform.services.EnrollmentService;
import edu.uth.online_course_platform.services.UserService;
import edu.uth.online_course_platform.until.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private JwtUtils jwtUtils;
    private final EnrollmentService enrollmentService;


    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    private String extractUsernameFromToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            assert header != null;
            return header.substring(7);
        }
        throw new AppException(ErrorCode.EMAIL_INVALID);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(HttpServletRequest request) {
        String token = extractUsernameFromToken(request);
        String email = jwtUtils.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return ResponseEntity.ok(UserResponseDto.fromEntity(user));
    }

    //lấy user theo id
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        UserResponseDto user = userService.getUserByID(id);
        return ResponseEntity.ok(user);
    }

    //tạo user mới
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody @Valid UserCreateDto userCreateDto) {
        UserResponseDto user = userService.createUser(userCreateDto);
        return ResponseEntity.ok(user);
    }

    //cập nhật user
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable long id ,@RequestBody @Valid UserCreateDto userCreateDto){
        UserResponseDto user = userService.updateUser(id, userCreateDto);
        return ResponseEntity.ok(user);
    }

    //xóa user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // đổi mật khẩu user
    @PutMapping("/{id}/change-password")
    public ResponseEntity<?> changePassword(@PathVariable long id, @RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
        userService.changePassword(id, changePasswordRequest);
        return ResponseEntity.ok("Cập nhật mật khẩu thành công ");
    }


    @GetMapping("/my-enrollments")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> myEnrollments() throws IllegalAccessException{
        List<EnrollmentResponse> res = enrollmentService.getEnrollmentsForCurrentUser();
        return ResponseEntity.ok(new ApiResponse<>(200, "My enrollments", res));
    }
}

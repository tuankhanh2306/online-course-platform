package edu.uth.online_course_platform.services;

import edu.uth.online_course_platform.dto.request.LoginRequest;
import edu.uth.online_course_platform.dto.request.RegisterRequest;
import edu.uth.online_course_platform.dto.response.LoginResponse;
import edu.uth.online_course_platform.dto.response.RegisterResponse;
import edu.uth.online_course_platform.exceptions.AppException;
import edu.uth.online_course_platform.exceptions.ErrorCode;
import edu.uth.online_course_platform.models.User;
import edu.uth.online_course_platform.repositories.UserRepository;
import edu.uth.online_course_platform.until.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // Tự động inject các dependency được khai báo final
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    /**
     * Xử lý logic đăng ký người dùng mới.
     */
    public RegisterResponse register(RegisterRequest request) {
        // 1. Kiểm tra xem email đã tồn tại chưa
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        // 2. Xử lý vai trò (role) một cách an toàn
        User.UserRole role = request.getRole();
        // Không cho phép người dùng tự đăng ký làm ADMIN
        if (role == null || role == User.UserRole.ADMIN) {
            role = User.UserRole.STUDENT; // Mặc định là STUDENT
        }

        // 3. Tạo đối tượng User mới
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        // 4. Lưu vào cơ sở dữ liệu
        userRepository.save(user);

        // 5. Trả về response theo DTO của dự án
        return new RegisterResponse(user.getFullName(), user.getEmail());
    }

    /**
     * Xử lý logic đăng nhập.
     */
    public LoginResponse login(LoginRequest request) {
        // 1. Xác thực người dùng bằng AuthenticationManager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2. Lấy thông tin người dùng đã được xác thực từ Principal
        User userPrincipal = (User) authentication.getPrincipal();

        // 3. Tạo JWT token
        String token = jwtUtils.generateToken( userPrincipal);

        // 4. Trả về response theo DTO của dự án (gồm token, fullName và role)
        return LoginResponse.builder()
                .token(token)
                .fullName(userPrincipal.getFullName())
                .role(userPrincipal.getRole().name())
                .build();
    }
}
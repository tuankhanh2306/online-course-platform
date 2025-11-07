package edu.uth.online_course_platform.services;

import edu.uth.online_course_platform.dto.request.ChangePasswordRequest;
import edu.uth.online_course_platform.dto.request.UserCreateDto;
import edu.uth.online_course_platform.dto.response.UserResponseDto;
import edu.uth.online_course_platform.exceptions.AppException;
import edu.uth.online_course_platform.exceptions.ErrorCode;
import edu.uth.online_course_platform.models.User;
import edu.uth.online_course_platform.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDto convertToDto(User user) {
        return UserResponseDto.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())   // thêm vào đây
                .role(user.getRole())
                .build();
    }


    // Tạo người dùng mới
    public UserResponseDto createUser(@Valid UserCreateDto userCreateDto) {
        if (userRepository.existsByEmail(userCreateDto.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_INVALID);
        }

        User user = User.builder()
                .fullName(userCreateDto.getFullName())
                .email(userCreateDto.getEmail())
                .password(passwordEncoder.encode(userCreateDto.getPassword()))
                .role(userCreateDto.getRole()) // 👈 bây giờ là UserRole
                .build();

        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    // Lấy thông tin người dùng theo ID
    public UserResponseDto getUserByID(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return convertToDto(user);
    }

    // Lấy thông tin người dùng theo email
    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return convertToDto(user);
    }

    // Lấy tất cả người dùng
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Cập nhật người dùng
    public UserResponseDto updateUser(Long id, UserCreateDto userCreateDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setFullName(userCreateDto.getFullName());
        user.setPhoneNumber(userCreateDto.getPhoneNumber());
        user.setEmail(userCreateDto.getEmail());
        user.setRole(userCreateDto.getRole());

        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    // Xóa người dùng
    public UserResponseDto deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        userRepository.delete(user);
        return convertToDto(user);
    }

    // Đổi mật khẩu
    public void changePassword(Long id, ChangePasswordRequest request){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}

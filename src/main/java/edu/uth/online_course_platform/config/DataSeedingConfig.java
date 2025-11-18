package edu.uth.online_course_platform.config;

import edu.uth.online_course_platform.models.User;
import edu.uth.online_course_platform.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Cấu hình này sẽ tự động chạy khi ứng dụng khởi động.
 * Nó dùng để "gieo mầm" (seed) dữ liệu ban đầu cho cơ sở dữ liệu.
 */
@Configuration
@RequiredArgsConstructor // Tự động inject các dependency qua constructor
public class DataSeedingConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataSeedingConfig.class);

    // Inject UserRepository để kiểm tra và lưu user
    private final UserRepository userRepository;

    // Inject PasswordEncoder để mã hóa mật khẩu
    // (Bean này phải được định nghĩa ở một file @Configuration khác, ví dụ: SecurityConfig)
    private final PasswordEncoder passwordEncoder;

    /**
     * Bean CommandLineRunner này sẽ được Spring Boot tự động thực thi.
     */
    @Bean
    public CommandLineRunner seedDatabase() {
        return args -> {

            // 1. Kiểm tra xem đã có user nào trong DB chưa
            // Chúng ta dùng count() thay vì findAll() để tối ưu hiệu suất
            if (userRepository.count() == 0) {

                logger.info("Cơ sở dữ liệu trống. Đang tạo tài khoản ADMIN mặc định...");

                // 2. Nếu chưa có, tạo user Admin
                User adminUser = User.builder()
                        .fullName("Admin User")
                        .email("admin@example.com")
                        .password(passwordEncoder.encode("admin123")) // Mã hóa mật khẩu!
                        .role(User.UserRole.ADMIN)
                        .build();

                // 3. Lưu user Admin vào CSDL
                userRepository.save(adminUser);

                logger.info("Đã tạo tài khoản ADMIN thành công: admin@example.com / admin123");

            } else {
                logger.info("Co so du lieu da co, bo qua viec seeding.");
            }
        };
    }
}
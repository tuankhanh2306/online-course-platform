package edu.uth.online_course_platform.config;

import edu.uth.online_course_platform.security.JwtAuthenticationEntryPoint; // <-- Thêm import này (nếu bạn có)
import edu.uth.online_course_platform.services.UserDetailsServiceImpl;
import edu.uth.online_course_platform.until.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor; // <-- Thêm import này
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; // <-- Thêm import này
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // <-- Thêm import này
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays; // Đổi List.of thành Arrays.asList()
import java.util.List;

@Configuration
@EnableWebSecurity // <-- Đảm bảo có annotation này
@EnableMethodSecurity(prePostEnabled = true) // Cho phép @PreAuthorize/@PostAuthorize
@RequiredArgsConstructor // <-- Sử dụng Lombok để tự động tạo constructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint; // <-- Inject JwtAuthenticationEntryPoint

    // Không cần constructor thủ công nữa nhờ @RequiredArgsConstructor

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Tắt CSRF cho REST API
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Cấu hình CORS
                // Cấu hình xử lý ngoại lệ và entry point cho JWT
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                // Cấu hình session management thành STATELESS (không dùng session)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Cấu hình phân quyền cho các HTTP requests
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/pages/**", "/js/**", "/css/**", "/images/**").permitAll()
                        // Các endpoint public (không cần xác thực)
                        .requestMatchers("/api/auth/**").permitAll()             // Đăng ký, Đăng nhập
                        .requestMatchers("/api/public/**").permitAll()           // Các tài nguyên công khai khác
                        .requestMatchers("/api/categories/**").permitAll()       // Lấy danh sách danh mục (bất kỳ ai)
                        .requestMatchers("/api/courses").permitAll()             // Lấy danh sách tất cả khóa học đã publish (bất kỳ ai)
                        .requestMatchers("/api/courses/{courseId}").permitAll()  // Xem chi tiết khóa học đã publish (bất kỳ ai)
                        // Các endpoint yêu cầu vai trò ADMIN

                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // Các endpoint yêu cầu vai trò INSTRUCTOR
                        .requestMatchers("/api/instructor/**").hasRole("INSTRUCTOR")
                        .requestMatchers("/").permitAll()
                        // Các endpoint yêu cầu vai trò STUDENT
                        .requestMatchers("/api/student/**").hasRole("STUDENT") // Ví dụ cho các API của học viên
                        // Tất cả các request khác (không khớp với các quy tắc trên) phải được xác thực
                        .anyRequest().authenticated()
                )
                // Thêm JWT filter vào trước filter xử lý xác thực Username/Password mặc định
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Cần phải rất cụ thể về các nguồn gốc được phép trong môi trường production
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:63342", "http://127.0.0.1:5500")); // Thêm http://127.0.0.1:5500 nếu bạn dùng Live Server
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept")); // Thêm một số header phổ biến
        configuration.setAllowCredentials(true); // Cho phép gửi cookies, authorization headers, v.v.

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Áp dụng cấu hình CORS cho tất cả các đường dẫn
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}